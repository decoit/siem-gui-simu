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

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.service.HeartbeatService;
import de.decoit.siemgui.service.UserAuthenticationService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


/**
 * Custom implementation of Spring's AuthenticationProvider to relay authentication to the RT REST API.
 * This implementation supports authentication with object of the type UsernamePasswordAuthenticationToken.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Component
public class RtAuthenticationProvider implements AuthenticationProvider {
	@Autowired
	private UserAuthenticationService userAuthenticationService;

	@Autowired
	private HeartbeatService hbServ;


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		User u;

		try {
			u = userAuthenticationService.login(name, password);
		}
		catch (ExternalServiceException ex) {
			throw new AuthenticationServiceException("Accessing RT user management failed", ex);
		}

		if (u.isSiemAuthorized()) {
			if (!u.isEnabled()) {
				throw new DisabledException("User is disabled: " + name);
			}

			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_SIEM_USER"));

			if (u.isSiemAdminAuthorized()) {
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_SIEM_ADMIN"));
			}

			RtAuthorizedUser authUser = new RtAuthorizedUser();

			authUser.setUsername(u.getUsername());
			authUser.setAuthenticated(true);
			authUser.setRtSessionId(u.getSessionId());
			authUser.setLastAnsweredHeartbeat(hbServ.getCurrentHeartbeat());
			authUser.setAlive(true);

			Authentication auth = new UsernamePasswordAuthenticationToken(authUser, password, grantedAuths);

			return auth;
		}
		else {
			return null;
		}
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
