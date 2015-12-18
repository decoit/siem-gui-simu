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

import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.service.ChartsService;
import de.decoit.siemgui.stomp.msgs.incoming.MsgChartRequest;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgChart;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;


/**
 * This controller provides mappings to STOMP message channels to request charts.
 * All operations are associated with the "charts" view.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class ChartsController {
	private final Logger LOG = LoggerFactory.getLogger(ChartsController.class.getName());

	@Autowired
	private ChartsService chartsServ;


	/**
	 * This mapping responds with the chart of active incidents per threat level.
	 * It is mapped to /charts/incidentsbythreatlevel and the response is sent to /queue/charts (user specific)
	 *
	 * @param reqMsg    Inbound message containing the chart index
	 * @param principal Authentication information provided by Spring Security
	 * @return STOMP message containing the chart information
	 *
	 * @throws ExternalServiceException if contacting external services failed
	 */
	@MessageMapping("/charts/incidentsbythreatlevel")
	@SendToUser("/queue/charts")
	@Secured("ROLE_SIEM_USER")
	public MsgChart requestChartIncidentsByThreatLevel(MsgChartRequest reqMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestChartIncidentsByThreatLevel, incoming message: " + reqMsg.toString());
		}

		MsgChart msg = chartsServ.getActiveIncidentsByThreatLevel(reqMsg.getChartIndex());

		if(LOG.isDebugEnabled()) {
			LOG.debug("requestChartIncidentsByThreatLevel, outgoing message: " + msg.toString());
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
		LOG.error("ExternalServiceException in EventsController", ex);
		return new MsgErrorNotification("Verbindung zu externem Dienst fehlgeschlagen, Ereignisse konnten nicht geladen werden!");
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
		LOG.error("RuntimeException in EventsController", ex);
		return new MsgErrorNotification("Es ist ein Fehler beim Laden von Ereignissen aufgetreten!");
	}
}
