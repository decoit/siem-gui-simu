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
package de.decoit.siemgui.security;

import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.service.UserAuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


/**
 * This LogoutHandler implementation is injected into the Spring Security logout procedure to properly close the session with RT REST API.
 * When the user logs out using the web interface, this handler will log him out from the RT REST API to prevent invalid open sessions inside of RT.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtSessionLogoutHandler implements LogoutHandler {
	private final Logger LOG = LoggerFactory.getLogger(RtSessionLogoutHandler.class.getName());

	@Autowired
	private UserAuthenticationService userAuthenticationService;


	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null) {
			RtAuthorizedUser authUser = (RtAuthorizedUser) (authentication.getPrincipal());

			try {
				userAuthenticationService.logout(authUser);
			}
			catch (ExternalServiceException ex) {
				LOG.error("Accessing RT user management failed, AuthorizedUser cleared but session not invalidated by RT", ex);
			}

			authUser.setRtSessionId(null);
			authUser.setUsername(null);
			authUser.setAuthenticated(false);
			authUser.setLastAnsweredHeartbeat(null);
			authUser.setAlive(false);
		}
	}
}
