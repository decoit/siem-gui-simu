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
package de.decoit.siemgui.web;

import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.HeartbeatService;
import de.decoit.siemgui.stomp.msgs.incoming.MsgHeartbeatAlive;
import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * This controller provides HTTP mapping to retrieve heartbeat alive notifications from users.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class HeartbeatController {
	private final Logger LOG = LoggerFactory.getLogger(HeartbeatController.class.getName());

	@Autowired
	private HeartbeatService hbServ;


	/**
	 * Retrieve an alive notification from a client.
	 * This method is mapped to the URI /heartbeat/alive
	 *
	 * @param alive     Inbound message containing the heartbeat ID to answer
	 * @param principal Authentication information provided by Spring Security
	 * @return HTTP response with empty body
	 */
	@RequestMapping(value = "/heartbeat/alive", method = RequestMethod.POST)
	@Secured("ROLE_SIEM_USER")
	@ResponseBody
	public String heartbeatAlive(@RequestBody MsgHeartbeatAlive alive, Principal principal) {
		AuthorizedUser actUser = (AuthorizedUser) ((Authentication) principal).getPrincipal();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Alive notification received:");
			LOG.debug("User: " + actUser.getUsername());
			LOG.debug("Heartbeat ID: " + alive.getHeartbeatId());
			LOG.debug("Current heartbeat ID: " + hbServ.getCurrentHeartbeat());
		}

		if (actUser.isAlive() && hbServ.validateClientAlive(actUser.getLastAnsweredHeartbeat())) {
			String hbId = alive.getHeartbeatId();

			if (hbServ.validateHeartbeatId(hbId)) {
				actUser.setLastAnsweredHeartbeat(hbId);
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Heartbeat ok, client alive");
			}
		}
		else {
			actUser.setAlive(false);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Client missed several heartbeats before this one, client considered dead");
			}
		}

		return "";
	}
}
