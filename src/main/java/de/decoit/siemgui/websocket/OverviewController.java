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
package de.decoit.siemgui.websocket;

import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.IncidentService;
import de.decoit.siemgui.service.TicketService;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgOverviewIncidents;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgOverviewTickets;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;


/**
 * This controller provides mappings to STOMP message channels to request information for the overview.
 * All operations are associated with the "overview" view.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class OverviewController {
	private final Logger LOG = LoggerFactory.getLogger(OverviewController.class.getName());

	@Autowired
	private TicketService ticketServ;

	@Autowired
	private IncidentService incidentServ;

	@Autowired
	ApplicationContext ctx;


	/**
	 * This mapping responds with the overview summary of ticket information.
	 * It maps to /overview/reqinfo/tickets and responds to /queue/overview/reqinfo/tickets (user specific)
	 *
	 * @param principal Authentication information provided by Spring Security
	 * @return STOMP message containing the summary
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/overview/reqinfo/tickets")
	@SendToUser("/queue/overview/reqinfo/tickets")
	@Secured("ROLE_SIEM_USER")
	public MsgOverviewTickets requestOverviewTickets(Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestOverviewTickets, no incoming message");
		}

		MsgOverviewTickets msg = new MsgOverviewTickets();

		AuthorizedUser actUser = (AuthorizedUser) ((Authentication) principal).getPrincipal();

		long reqStart = System.nanoTime();
		List<Ticket> allTickets = ticketServ.getAllSiemTickets(TicketFields.CREATED_ON, OrderByDirection.ASCENDING, actUser);
		long reqEnd = System.nanoTime();

		long mapStart = System.nanoTime();
		Map<TicketStatus, List<Ticket>> ticketMap = allTickets.stream().collect(Collectors.groupingBy(Ticket::getStatus, Collectors.toList()));
		long mapEnd = System.nanoTime();

		long filterStart = System.nanoTime();
		Map<TicketStatus, List<Ticket>> myTicketsMap = allTickets.stream().filter(t -> t.getOwner().getUsername().equals(actUser.getUsername()) == true).collect(Collectors.groupingBy(Ticket::getStatus, Collectors.toList()));
		long filterEnd = System.nanoTime();

		if (LOG.isDebugEnabled()) {
			LOG.debug("SIEM tickets request time: " + ((reqEnd - reqStart) / 1000) + "us");
			LOG.debug("SIEM tickets mapping time: " + ((mapEnd - mapStart) / 1000) + "us");
			LOG.debug("SIEM tickets filter+mapping time: " + ((filterEnd - filterStart) / 1000) + "us");
		}

		int newTicketsCount = 0;
		int openTicketsCount = 0;
		int resolvedTicketsCount = 0;

		if (ticketMap.get(TicketStatus.NEW) != null) {
			newTicketsCount = ticketMap.get(TicketStatus.NEW).size();
		}
		if (ticketMap.get(TicketStatus.OPEN) != null) {
			openTicketsCount = ticketMap.get(TicketStatus.OPEN).size();
		}
		if (ticketMap.get(TicketStatus.RESOLVED) != null) {
			resolvedTicketsCount = ticketMap.get(TicketStatus.RESOLVED).size();
		}

		msg.setTicketsNew(newTicketsCount);
		msg.setTicketsOpen(openTicketsCount);
		msg.setTicketsResolved(resolvedTicketsCount);

		int myNewTicketsCount = 0;
		int myOpenTicketsCount = 0;
		int myResolvedTicketsCount = 0;

		if (myTicketsMap.get(TicketStatus.NEW) != null) {
			myNewTicketsCount = myTicketsMap.get(TicketStatus.NEW).size();
		}
		if (myTicketsMap.get(TicketStatus.OPEN) != null) {
			myOpenTicketsCount = myTicketsMap.get(TicketStatus.OPEN).size();
		}
		if (myTicketsMap.get(TicketStatus.RESOLVED) != null) {
			myResolvedTicketsCount = myTicketsMap.get(TicketStatus.RESOLVED).size();
		}

		msg.setMyTicketsNew(myNewTicketsCount);
		msg.setMyTicketsOpen(myOpenTicketsCount);
		msg.setMyTicketsResolved(myResolvedTicketsCount);

		if (LOG.isDebugEnabled()) {
			LOG.debug("requestOverviewTickets, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping responds with the overview summary of incident information.
	 * It maps to /overview/reqinfo/incidents and responds to /queue/overview/reqinfo/incidents (user specific)
	 *
	 * @param principal Authentication information provided by Spring Security
	 * @return STOMP message containing the summary
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/overview/reqinfo/incidents")
	@SendToUser("/queue/overview/reqinfo/incidents")
	@Secured("ROLE_SIEM_USER")
	public MsgOverviewIncidents requestOverviewIncidents(Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestOverviewIncidents, no incoming message");
		}

		Map<String, Long> incidents = incidentServ.countIncidentsForOverview();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Counted incidents: " + incidents);
		}

		MsgOverviewIncidents msg = new MsgOverviewIncidents();

		msg.setIncidentsNew(incidents.get("new"));
		msg.setIncidentsInProgress(incidents.get("inProgress"));
		msg.setIncidentsUnknown(incidents.get("unknown"));

		msg.setIncidentsLowRisk(incidents.get("lowRisk"));
		msg.setIncidentsMediumRisk(incidents.get("mediumRisk"));
		msg.setIncidentsHighRisk(incidents.get("highRisk"));

		if (LOG.isDebugEnabled()) {
			LOG.debug("requestOverviewIncidents, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This method catches ExternalServiceException objects thrown by the mapping methods.
	 * It sends an error notification to /queue/error (user specific) to notify the user of the failure.
	 *
	 * @param ex Caught exception
	 * @return Error notification message
	 */
	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public MsgErrorNotification handleException(ExternalServiceException ex) {
		LOG.error("ExternalServiceException in OverviewController", ex);
		return new MsgErrorNotification("Verbindung zu externem Dienst fehlgeschlagen, Daten konnten nicht geladen werden!");
	}


	/**
	 * This method catches RuntimeException objects thrown by the mapping methods.
	 * It sends an error notification to /queue/error (user specific) to notify the user of the failure.
	 *
	 * @param ex Caught exception
	 * @return Error notification message
	 */
	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public MsgErrorNotification handleException(RuntimeException ex) {
		LOG.error("RuntimeException in OverviewController", ex);
		return new MsgErrorNotification("Es ist ein Fehler beim Laden von Daten aufgetreten!");
	}
}
