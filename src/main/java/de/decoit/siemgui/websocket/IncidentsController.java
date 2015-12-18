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

import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgIncidentsList;
import de.decoit.siemgui.stomp.msgs.incoming.MsgIncidentRequest;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.IncidentService;
import de.decoit.siemgui.stomp.msgs.incoming.MsgIncidentTimeToBook;
import de.decoit.siemgui.stomp.msgs.incoming.MsgPostCommentRequest;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgCommentPosted;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgTicketHistory;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;


/**
 * This controller provides mappings to STOMP message channels to request incident information.
 * All operations are associated with the "incidents view.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class IncidentsController extends AbstractControllerHelper {
	private final Logger LOG = LoggerFactory.getLogger(IncidentsController.class.getName());

	@Autowired
	IncidentService incidentServ;


	/**
	 * This mapping responds with the list of active incidents.
	 * It maps to /incidents/listactive and responds to /queue/incidents/activelist (user specific)
	 *
	 * @return STOMP message containing the incident list
	 */
	@MessageMapping("/incidents/listactive")
	@SendToUser("/queue/incidents/activelist")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList requestActiveIncidents() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestActiveIncidents, no incoming message");
		}

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> actList = incidentServ.getActiveIncidents();
		msg.setActiveIncList(actList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("requestActiveIncidents, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping responds with the list of resolved incidents.
	 * It maps to /incidents/listresolved and responds to /queue/incidents/resolvedlist (user specific)
	 *
	 * @return STOMP message containing the incident list
	 */
	@MessageMapping("/incidents/listresolved")
	@SendToUser("/queue/incidents/resolvedlist")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList requestResolvedIncidents() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestResolvedIncidents, no incoming message");
		}

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> resList = incidentServ.getResolvedIncidents();
		msg.setResolvedIncList(resList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("requestResolvedIncidents, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping answers a request to fetch the ticket history for a specific incident.
	 * It responds with a list of ticket history items belonging to the incident's ticket.
	 * It maps to /incidents/requesthistory and responds to /queue/incidents/history (user specific)
	 *
	 * @param inMsg Inbound message containing the incident ID
	 * @param principal Authentication information provided by Spring Security
	 * @return STOMP message containing the ticket history item list
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/incidents/requesthistory")
	@SendToUser("/queue/incidents/history")
	@Secured("ROLE_SIEM_USER")
	public MsgTicketHistory requestTicketHistory(MsgIncidentRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestTicketHistory, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		List<TicketHistoryItem> historyList = incidentServ.getHistoryForIncident(inMsg.getIncidentId(), actUser);

		MsgTicketHistory msg = new MsgTicketHistory(inMsg.getIncidentId(), historyList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("requestTicketHistory, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping retrieves a request to take a specific incident.
	 * It responds with an up to date list of incidents.
	 * It maps to /incidents/take and responds to /queue/incidents/takeresult (user specific)
	 *
	 * @param inMsg     Inbound message containing the incident ID
	 * @param principal Authentication information provided by Spring Security
	 * @return Updated list of incidents
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/incidents/take")
	@SendToUser("/queue/incidents/takeresult")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList takeIncident(MsgIncidentRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("takeIncident, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		incidentServ.takeIncident(inMsg.getIncidentId(), actUser);

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> actList = incidentServ.getActiveIncidents();
		List<Incident> resList = incidentServ.getResolvedIncidents();
		msg.setActiveIncList(actList);
		msg.setResolvedIncList(resList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("takeIncident, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping retrieves a request to begin work on a specific incident.
	 * It responds with an up to date list of incidents.
	 * It maps to /incidents/beginwork and responds to /queue/incidents/beginresult (user specific)
	 *
	 * @param inMsg     Inbound message containing the incident ID
	 * @param principal Authentication information provided by Spring Security
	 * @return Updated list of incidents
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/incidents/beginwork")
	@SendToUser("/queue/incidents/beginresult")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList beginWorkOnIncident(MsgIncidentRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("beginWorkOnIncident, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		incidentServ.beginWorkOnIncident(inMsg.getIncidentId(), actUser);

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> actList = incidentServ.getActiveIncidents();
		List<Incident> resList = incidentServ.getResolvedIncidents();
		msg.setActiveIncList(actList);
		msg.setResolvedIncList(resList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("beginWorkOnIncident, outgoing message: " + msg.toString());
		}

		return msg;
	}


	/**
	 * This mapping retrieves a request to finish work on a specific incident.
	 * It responds with an up to date list of incidents.
	 * It maps to /incidents/finishwork and responds to /queue/incidents/finishresult (user specific)
	 *
	 * @param inMsg     Inbound message containing the incident ID
	 * @param principal Authentication information provided by Spring Security
	 * @return Updated list of incidents
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/incidents/finishwork")
	@SendToUser("/queue/incidents/finishresult")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList finishWorkOnIncident(MsgIncidentRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("finishWorkOnIncident, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		incidentServ.finishWorkOnIncident(inMsg.getIncidentId(), actUser);

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> actList = incidentServ.getActiveIncidents();
		List<Incident> resList = incidentServ.getResolvedIncidents();
		msg.setActiveIncList(actList);
		msg.setResolvedIncList(resList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("finishWorkOnIncident, outgoing message: " + msg.toString());
		}

		return msg;
	}


	@MessageMapping("/incidents/postcomment")
	@SendToUser("/queue/incidents/commentconfirmed")
	@Secured("ROLE_SIEM_USER")
	public MsgCommentPosted postCommentOnIncident(MsgPostCommentRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("postCommentOnIncident, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		boolean result = incidentServ.postCommentOnIncident(inMsg.getIncidentId(), inMsg.getCommentText(), actUser);

		MsgCommentPosted msg = new MsgCommentPosted();
		msg.setIncidentId(inMsg.getIncidentId());
		msg.setSuccess(result);

		if (LOG.isDebugEnabled()) {
			LOG.debug("postCommentOnIncident, outgoing message: " + msg.toString());
		}

		return msg;
	}


	@MessageMapping("/incidents/booktime")
	@SendToUser("/queue/incidents/timebooked")
	@Secured("ROLE_SIEM_USER")
	public MsgIncidentsList bookTimeOnIncident(MsgIncidentTimeToBook inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("bookTimeOnIncident, incoming message: " + inMsg.toString());
		}

		AuthorizedUser actUser = convertPrincipalToAuthorizedUser(principal);

		incidentServ.bookTimeOnIncident(inMsg.getIncidentId(), inMsg.getTimeToBook(), actUser);

		MsgIncidentsList msg = new MsgIncidentsList();

		List<Incident> actList = incidentServ.getActiveIncidents();
		List<Incident> resList = incidentServ.getResolvedIncidents();
		msg.setActiveIncList(actList);
		msg.setResolvedIncList(resList);

		if (LOG.isDebugEnabled()) {
			LOG.debug("bookTimeOnIncident, outgoing message: " + msg.toString());
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
		LOG.error("ExternalServiceException in IncidentsController", ex);
		return new MsgErrorNotification("Verbindung zu externem Dienst fehlgeschlagen, Vorgang konnte nicht ausgeführt werden!");
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
		LOG.error("RuntimeException in IncidentsController", ex);
		return new MsgErrorNotification("Es ist ein Fehler beim Verarbeiten des Vorgangs aufgetreten!");
	}
}
