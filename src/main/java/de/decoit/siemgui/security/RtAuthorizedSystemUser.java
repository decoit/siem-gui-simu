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
import de.decoit.siemgui.service.UserAuthenticationService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * This class extends the RtAuthorizedUser by adding a way to authenticate with RT without the usage of Spring Security.
 * It is used for the RT SIEM system user account that is used to create tickets. This user does not login through
 * Spring Security and thus must to be able to do that login to the RT REST API itself. AutoCloseable is implemented
 * to make sure that the RT session is destroyed when the object is removed, for example if the application is stopped.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(exclude = { "userAuthServ" }, callSuper = true)
public class RtAuthorizedSystemUser extends RtAuthorizedUser implements AutoCloseable {
	private String password;
	private UserAuthenticationService userAuthServ;


	public RtAuthorizedSystemUser() {
		super();
	}

	/**
	 * Login the system user into RT.
	 * Username and password stored in the object are used.
	 *
	 * @throws ExternalServiceException if connecting to RT failed
	 */
	public void login() throws ExternalServiceException {
		User u = userAuthServ.login(this.username, this.password);

		this.rtSessionId = u.getSessionId();
		this.authenticated = true;
		this.password = null;
	}


	/**
	 * Do a clean logout from RT if this object gets destroyed.
	 *
	 * @throws Exception if connecting to RT failed
	 */
	@Override
	public void close() throws Exception {
		userAuthServ.logout(this);
	}
}
