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

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.UserDetailsService;
import de.decoit.siemgui.stomp.msgs.incoming.MsgUserEditRequest;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgErrorNotification;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgUserEditSuccess;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgUserList;
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
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class UsersAdminController extends AbstractControllerHelper {
	private final Logger LOG = LoggerFactory.getLogger(UsersAdminController.class.getName());

	@Autowired
	private UserDetailsService userServ;


	@MessageMapping("/admin/users/list")
	@SendToUser("/queue/admin/users/list")
	@Secured("ROLE_SIEMADMIN")
	public MsgUserList requestUserList(Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("requestUserList, no incoming message");
		}

		AuthorizedUser authUser = convertPrincipalToAuthorizedUser(principal);
		List<User> userList = userServ.getAllUserDetails(authUser);

		MsgUserList msg = new MsgUserList();
		msg.setUserlist(userList);

		if(LOG.isDebugEnabled()) {
			LOG.debug("requestUserList, outgoing message: " + msg.toString());
		}

		return msg;
	}


	@MessageMapping("/admin/users/store")
	@SendToUser("/queue/admin/users/storesuccess")
	@Secured("ROLE_SIEMADMIN")
	public MsgUserEditSuccess editUser(MsgUserEditRequest inMsg, Principal principal) throws ExternalServiceException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("editUser, incoming message: " + inMsg.toString());
		}

		AuthorizedUser authUser = convertPrincipalToAuthorizedUser(principal);
		User tmpUpdUser = userServ.getUserDetailsById(inMsg.getUserId(), authUser);
		tmpUpdUser.setSiemAuthorized(inMsg.isSiemAuthorized());
		tmpUpdUser.setSiemAdminAuthorized(inMsg.isSiemAdminAuthorized());

		User updUser = userServ.updateUser(tmpUpdUser, authUser);
		boolean success = (updUser != null);

		MsgUserEditSuccess msg = new MsgUserEditSuccess();
		msg.setSuccess(success);

		if(LOG.isDebugEnabled()) {
			LOG.debug("editUser, outgoing message: " + msg.toString());
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
		LOG.error("ExternalServiceException in UsersAdminController", ex);
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
		LOG.error("RuntimeException in UsersAdminController", ex);
		return new MsgErrorNotification("Es ist ein Fehler beim Verarbeiten des Vorgangs aufgetreten!");
	}
}
