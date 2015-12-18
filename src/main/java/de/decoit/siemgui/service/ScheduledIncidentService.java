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

import com.google.common.collect.Lists;
import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.dao.incidents.IncidentDao;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.domain.tickethistory.ContentText;
import de.decoit.siemgui.domain.tickethistory.ContentTextLine;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.exception.OperationNotAllowedException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


/**
 * Implementation of the IncidentService that uses the scheduler of Spring to fetch incidents from the database.
 * The scheduler is activated by using the @Scheduled annotation and is set to an interval of 60 seconds.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class ScheduledIncidentService implements IncidentService {
	private final Logger LOG = LoggerFactory.getLogger(ScheduledIncidentService.class.getName());
	private final Map<Long, Incident> incidentMap = new HashMap<>();
	private long lastIncidentId = -1;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	IncidentDao incidentDao;

	@Autowired
	PushNotificationService pushNoteServ;

	@Autowired
	TicketService ticketServ;

	@Autowired
	UserDetailsService userDetailsServ;

	@Autowired
	SystemConfig sysConf;


	@Override
	public Incident getIncident(long id) {
		return incidentMap.get(id);
	}


	@Override
	public Map<String, Long> countIncidentsForOverview() {
		Collection<Incident> incCollection = incidentMap.values();

		// Count incidents by status
		long incidentsNew = incCollection.stream().filter(i -> i.getStatus() == IncidentStatus.NEW).count();
		long incidentsInProgress = incCollection.stream().filter(i -> i.getStatus() == IncidentStatus.IN_PROGRESS).count();
		long incidentsUnknown = incCollection.stream().filter(i -> i.getStatus() == IncidentStatus.UNKNOWN).count();

		// Count incidents by risk, limits are configured in properties file
		long incidentsLowRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.LOW).count();
		long incidentsMediumRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.MEDIUM).count();
		long incidentsHighRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.HIGH).count();

		Map<String, Long> rv = new HashMap<>();

		rv.put("new", incidentsNew);
		rv.put("inProgress", incidentsInProgress);
		rv.put("unknown", incidentsUnknown);

		rv.put("lowRisk", incidentsLowRisk);
		rv.put("mediumRisk", incidentsMediumRisk);
		rv.put("highRisk", incidentsHighRisk);

		return rv;
	}


	@Override
	public Map<ThreatLevel, Long> calculateThreatLevel() {
		Collection<Incident> incCollection = incidentMap.values();

		long incidentsLowRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.LOW).count();
		long incidentsMediumRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.MEDIUM).count();
		long incidentsHighRisk = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).filter(i -> i.getThreatLevel() == ThreatLevel.HIGH).count();

		Map<ThreatLevel, Long> rv = new HashMap<>();

		rv.put(ThreatLevel.LOW, incidentsLowRisk);
		rv.put(ThreatLevel.MEDIUM, incidentsMediumRisk);
		rv.put(ThreatLevel.HIGH, incidentsHighRisk);

		return rv;
	}


	@Override
	public List<Incident> getActiveIncidents() {
		Collection<Incident> incCollection = incidentMap.values();

		List<Incident> rv = incCollection.stream().filter(i -> i.getStatus() != IncidentStatus.DONE).collect(Collectors.toList());

		return rv;
	}


	@Override
	public List<Incident> getResolvedIncidents() {
		Collection<Incident> incCollection = incidentMap.values();

		List<Incident> rv = incCollection.stream().filter(i -> i.getStatus() == IncidentStatus.DONE).collect(Collectors.toList());

		return rv;
	}


	@Override
	public List<TicketHistoryItem> getHistoryForIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException {
		Ticket ticket = incidentMap.get(incidentId).getTicket();

		List<TicketHistoryItem> history = ticketServ.getTicketHistory(ticket, user);

		return history;
	}


	@Override
	public void takeIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException {
		Incident inc = incidentMap.get(incidentId);

		if (inc.getTicket().getOwner().getUsername().equals("Nobody")) {
			User owner = userDetailsServ.getUserDetails(user.getUsername(), user);

			inc.getTicket().setOwner(owner);

			Ticket updTicket = ticketServ.updateTicket(inc.getTicket(), user);
			inc.setTicket(updTicket);
		}
	}


	@Override
	public void beginWorkOnIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException {
		Incident inc = incidentMap.get(incidentId);

		// Ticket status change is only allowed if the current user owns the ticket and the current status is "NEW"
		if (inc.getTicket().getOwner().getUsername().equals(user.getUsername())) {
			if (inc.getStatus() == IncidentStatus.NEW) {
				// First try to update the ticket's status
				inc.getTicket().setStatus(TicketStatus.OPEN);

				Ticket updTicket = ticketServ.updateTicket(inc.getTicket(), user);
				inc.setTicket(updTicket);

				// If ticket update did not fail, update the incident now
				inc.setStatus(IncidentStatus.IN_PROGRESS);
				Incident outInc = incidentDao.updateIncident(inc);

				// Attach the updated ticket to the new Incident object and put it into the map
				outInc.setTicket(updTicket);
				incidentMap.put(outInc.getId(), outInc);
			}
			else {
				throw new IllegalStateException("Incident must have status NEW to allow begin working on it");
			}
		}
		else {
			throw new OperationNotAllowedException("You must be the owner of the ticket to update it");
		}
	}


	@Override
	public void finishWorkOnIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException {
		Incident inc = incidentMap.get(incidentId);

		// Ticket status change is only allowed if the current user owns the ticket and the current status is "IN_PROGRESS"
		if (inc.getTicket().getOwner().getUsername().equals(user.getUsername())) {
			if (inc.getStatus() == IncidentStatus.IN_PROGRESS) {
				// First try to update the ticket's status
				inc.getTicket().setStatus(TicketStatus.RESOLVED);

				Ticket updTicket = ticketServ.updateTicket(inc.getTicket(), user);
				inc.setTicket(updTicket);

				// If ticket update did not fail, update the incident now
				inc.setStatus(IncidentStatus.DONE);
				Incident outInc = incidentDao.updateIncident(inc);

				// Attach the updated ticket to the new Incident object and put it into the map
				outInc.setTicket(updTicket);
				incidentMap.put(outInc.getId(), outInc);
			}
			else {
				throw new IllegalStateException("Incident must have status IN_PROGRESS to allow finish working on it");
			}
		}
		else {
			throw new OperationNotAllowedException("You must be the owner of the ticket to update it");
		}
	}


	@Override
	public boolean postCommentOnIncident(long incidentId, String commentText, AuthorizedUser user) throws ExternalServiceException {
		Incident inc = incidentMap.get(incidentId);
		String[] commentParts = commentText.split("\n");

		if(LOG.isDebugEnabled()) {
			LOG.debug("postCommentOnIncident, Incident to post comment on: " + inc.toString());
			LOG.debug("commentText: " + commentText);
			LOG.debug("commentParts: " + Arrays.toString(commentParts));
		}

		// TODO: Does not work properly
		ContentText content = new ContentText();

		for(String s : commentParts) {
			if(s.length() > 0) {
				ContentTextLine line = new ContentTextLine(s);
				content.addElement(line);
			}
		}

		return ticketServ.postTicketComment(inc.getTicket(), content, user);
	}


	@Override
	public int bookTimeOnIncident(long incidentId, int minutes, AuthorizedUser user) throws ExternalServiceException {
		if(minutes <= 0) {
			throw new IllegalArgumentException("Number of minutes to book must be greater than 0, was" + minutes);
		}

		Incident inc = incidentMap.get(incidentId);

		// Ticket status change is only allowed if the current user owns the ticket and the current status is "IN_PROGRESS"
		if (inc.getTicket().getOwner().getUsername().equals(user.getUsername())) {
			if (inc.getStatus() == IncidentStatus.IN_PROGRESS) {
				Ticket ticket = inc.getTicket();

				int timeWorked = ticket.getTimeWorked();
				timeWorked += minutes;
				ticket.setTimeWorked(timeWorked);

				Ticket updTicket = ticketServ.updateTicket(ticket, user);
				inc.setTicket(updTicket);

				return updTicket.getTimeWorked();
			}
			else {
				throw new IllegalStateException("Incident must have status IN_PROGRESS to book time on it");
			}
		}
		else {
			throw new OperationNotAllowedException("You must be the owner of the ticket to update it");
		}
	}


	@Override
	@Scheduled(fixedRate = 60000)
	public void incidentLookup() {
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Started incident lookup");
			}

			List<Incident> resultList = incidentDao.getNewIncidents(lastIncidentId);

			// Update the incident map with new incident and ticket information
			for (Incident i : resultList) {
				Ticket t = ticketServ.getTicketForIncident(i);
				i.setTicket(t);

				incidentMap.put(i.getId(), i);
			}

			// Convert the list into a list of incident IDs
			List<Long> incIdList = resultList.stream().map(Incident::getId).collect(Collectors.toList());

			if (!incIdList.isEmpty()) {
				// Store new highest incident ID
				Collections.sort(incIdList);
				List<Long> reversedIncIdList = Lists.reverse(incIdList);
				lastIncidentId = reversedIncIdList.get(0);

				// Send push notification
				StringBuilder sb = new StringBuilder();
				sb.append(incIdList.size());

				if (incIdList.size() == 1) {
					sb.append(" neuer Vorfall gefunden!");
				}
				else {
					sb.append(" neue Vorfälle gefunden!");
				}

				pushNoteServ.sendPushNotification(sb.toString());
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Incident lookup finished: " + incIdList + " new");
			}
		}
		catch (ExternalServiceException ex) {
			LOG.debug("ExternalServiceException in scheduled method", ex);
		}
		catch (RuntimeException ex) {
			LOG.debug("RuntimeException in scheduled method", ex);
		}
	}
}
