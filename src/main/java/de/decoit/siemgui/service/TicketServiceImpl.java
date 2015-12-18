/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.siemgui.service;

import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.TicketDao;
import de.decoit.siemgui.dao.tickets.TicketQueueDao;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.dao.tickets.filter.TicketFilter;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentRecommendation;
import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.tickethistory.ContentText;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedSystemUser;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


/**
 * An implementation of the TicketService the uses a write-through strategy.
 * All action are executed directly on the ticket system, no caching is implemented.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class TicketServiceImpl implements TicketService {
	private final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class.getName());

	@Autowired
	ApplicationContext ctx;

	@Autowired
	SystemConfig sysConf;

	@Autowired
	TicketDao ticketDao;

	@Autowired
	TicketQueueDao ticketQueueDao;

	@Autowired
	RtAuthorizedSystemUser authSysUser;


	@Override
	public List<Ticket> getAllSiemTickets(TicketFields orderByField, OrderByDirection orderByDirection, AuthorizedUser user) throws ExternalServiceException {
		TicketFilter filter = ctx.getBean(TicketFilter.class);

		filter
				.queueIs(sysConf.getSiemTicketQueueName())
				.and()
				.leftParenthesis()
				.statusIs(TicketStatus.NEW)
				.or()
				.statusIs(TicketStatus.OPEN)
				.or()
				.statusIs(TicketStatus.RESOLVED)
				.rightParenthesis();

		List<Ticket> list = ticketDao.searchTickets(filter, orderByField, orderByDirection, user);

		return list;
	}


	@Override
	public List<Ticket> getSiemTicketsWithStatus(TicketStatus status, TicketFields orderByField, OrderByDirection orderByDirection, AuthorizedUser user) throws ExternalServiceException {
		TicketFilter filter = ctx.getBean(TicketFilter.class);

		filter
				.queueIs(sysConf.getSiemTicketQueueName())
				.and()
				.statusIs(status);

		List<Ticket> list = ticketDao.searchTickets(filter, orderByField, orderByDirection, user);

		return list;
	}


	@Override
	public Ticket getTicketForIncident(Incident inc) throws ExternalServiceException {
		Ticket ticket;

		TicketFilter filter = ctx.getBean(TicketFilter.class);

		filter
				.queueIs(sysConf.getSiemTicketQueueName())
				.and()
				.incidentIs(inc.getId());

		List<Ticket> result = ticketDao.searchTickets(filter, TicketFields.SUBJECT, OrderByDirection.ASCENDING, authSysUser);

		if (result.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No ticket for incident ID " + inc.getId());
			}

			ticket = createTicketForIncident(inc);
		}
		else if (result.size() == 1) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Found ticket for incident ID " + inc.getId());
			}

			ticket = result.get(0);
		}
		else {
			throw new ExternalServiceException("Multiple tickets (" + result.size() + ") for incident ID " + inc.getId() + " returned");
		}

		return ticket;
	}


	@Override
	public List<TicketHistoryItem> getTicketHistory(Ticket ticket, AuthorizedUser user) throws ExternalServiceException {
		List<TicketHistoryItem> history = ticketDao.getTicketHistory(ticket.getId(), user);

		return history;
	}


	@Override
	public Ticket updateTicket(Ticket ticket, AuthorizedUser user) throws ExternalServiceException {
		boolean success = ticketDao.updateTicket(ticket, user);

		if(!success) {
			throw new ExternalServiceException("Update ticket task returned false");
		}

		Ticket updTicket = ticketDao.getTicketDetails(ticket.getId(), user);

		return updTicket;
	}


	@Override
	public TicketQueue getTicketQueueDetails(String queueName, AuthorizedUser user) throws ExternalServiceException {
		TicketQueue tq = ticketQueueDao.getTicketQueueDetails(queueName, user);

		return tq;
	}


	@Override
	public boolean postTicketComment(Ticket ticket, ContentText comment, AuthorizedUser user) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("postTicketComment, Ticket to post comment on: " + ticket.toString());
		}

		String commentText = comment.toString();

		boolean result  = ticketDao.commentTicket(ticket.getId(), commentText, user);

		return result;
	}


	/**
	 * Create a new ticket for a specific incident.
	 *
	 * @param inc Incident to create a ticket for
	 * @return The created ticket
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	private Ticket createTicketForIncident(Incident inc) throws ExternalServiceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating ticket for incident ID " + inc.getId());
		}

		LocalDateTime dueOn = inc.getTimestamp();
		switch(inc.getThreatLevel()) {
			case LOW:
				dueOn = dueOn.plus(sysConf.getThreatLevelLowDueIn());
				break;
			case MEDIUM:
				dueOn = dueOn.plus(sysConf.getThreatLevelMedDueIn());
				break;
			case HIGH:
				dueOn = dueOn.plus(sysConf.getThreatLevelHighDueIn());
				break;
			default:
				throw new IllegalStateException("No thread level calculated on incident");
		}


		NewTicket tmpTicket = new NewTicket();
		TicketQueue queue = ticketQueueDao.getTicketQueueDetails(sysConf.getSiemTicketQueueName(), authSysUser);

		StringBuilder sb = new StringBuilder("[Incident ");
		sb.append(inc.getId());
		sb.append("] ");
		sb.append(inc.getName());

		StringBuilder sbText = new StringBuilder("[p]");
		sbText.append(inc.getDescription());
		sbText.append("[/p]");

		if(!StringUtils.isBlank(inc.getRecommendation())) {
			sbText.append("[p]");
			sbText.append(inc.getRecommendation());
			sbText.append("[/p]");
		}

//		for(IncidentRecommendation iRec : inc.getRecommendations()) {
//			StringBuilder sbRec = new StringBuilder("[p]");
//			sbRec.append(iRec.getRecommendation());
//			sbRec.append("[/p]");
//
//			sbText.append(sbRec);
//		}

		tmpTicket.setTitle(sb.toString());
		tmpTicket.setQueue(queue);
		tmpTicket.setText(sbText.toString());
		tmpTicket.setRequestor(authSysUser.getUsername());
		tmpTicket.setStarts(LocalDateTime.now());
		tmpTicket.setIncidentId(inc.getId());
		tmpTicket.setRisk(inc.getRisk());
		tmpTicket.setDueOn(dueOn);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Ticket recommendation text: " + tmpTicket.getText());
		}

		long newId = ticketDao.createTicket(tmpTicket, authSysUser);

		return ticketDao.getTicketDetails(newId, authSysUser);
	}
}
