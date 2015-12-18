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
package de.decoit.siemgui.stomp.interceptors;

import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.HeartbeatService;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


/**
 * A subclass of ChannelInterceptorAdapter that checks if a user sending a message to the backend is considered alive.
 * This interceptor is registered on the inbound channel of Spring Messaging. If a user is considered dead the message
 * is dropped without a notification.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Component
public class ClientAliveInterceptor extends ChannelInterceptorAdapter {
	private final Logger LOG = LoggerFactory.getLogger(ClientAliveInterceptor.class.getName());
	private HeartbeatService hbServ;

	@Autowired
	private ApplicationContext ctx;


	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(message.toString());
		}

		if(hbServ == null) {
			// HeartbeatService bean must be requested here to prevent circular dependency during construction
			hbServ = ctx.getBean(HeartbeatService.class);
		}

		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);

		if (sha.getCommand() != StompCommand.CONNECT && sha.getCommand() != StompCommand.DISCONNECT && sha.getCommand() != StompCommand.UNSUBSCRIBE) {
			AuthorizedUser actUser = (AuthorizedUser) ((Authentication) sha.getUser()).getPrincipal();

			if (actUser.isAlive()) {
				if (hbServ.validateClientAlive(actUser.getLastAnsweredHeartbeat())) {
					return message;
				}
				else {
					actUser.setAlive(false);

					StringBuilder logMsg = new StringBuilder("User '");
					logMsg.append(actUser.getUsername());
					logMsg.append("' is now considered dead.");

					LOG.warn(logMsg.toString());
				}
			}

			// This will happen if client is no longer alive
			SimpMessagingTemplate smt = ctx.getBean(SimpMessagingTemplate.class);

			MsgErrorNotification msg = new MsgErrorNotification("Ihre Sitzung ist abgelaufen, bitte melden Sie sich erneut an! Sie werden in wenigen Sekunden abgemeldet.", true);
			smt.convertAndSendToUser(sha.getUser().getName(), "/queue/errors", msg);

			StringBuilder logMsg = new StringBuilder("Session of user '");
			logMsg.append(actUser.getUsername());
			logMsg.append("' considered dead, message dropped");
			LOG.warn(logMsg.toString());

			if (LOG.isDebugEnabled()) {
				LOG.debug(message.toString());
			}

			return null;
		}
		else {
			return message;
		}
	}
}
