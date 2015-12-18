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

import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgThreatLevel;
import de.decoit.siemgui.service.IncidentService;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;


/**
 * This controller provides mappings to STOMP message channels to request information that is not associated with any view.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class GlobalViewController {
	private final Logger LOG = LoggerFactory.getLogger(GlobalViewController.class.getName());

	@Autowired
	IncidentService incServ;


	/**
	 * This mapping responds with the threat level summary.
	 * It is mapped to /threatlevel and responds to /topic/threatlevel (global)
	 *
	 * @return STOMP message containing the summary
	 */
	@MessageMapping("/threatlevel")
	@SendTo("/topic/threatlevel")
	public MsgThreatLevel sendThreatLevel() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("sendThreatLevel, no incoming message");
		}

		Map<ThreatLevel, Long> riskMap = incServ.calculateThreatLevel();

		MsgThreatLevel msg = new MsgThreatLevel(riskMap.get(ThreatLevel.HIGH), riskMap.get(ThreatLevel.MEDIUM));

		if(LOG.isDebugEnabled()) {
			LOG.debug("sendThreatLevel, outgoing message: " + msg.toString());
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
		LOG.error("ExternalServiceException in GlobalViewController", ex);
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
		LOG.error("RuntimeException in GlobalViewController", ex);
		return new MsgErrorNotification("Es ist ein Fehler beim Laden von Daten aufgetreten!");
	}
}
