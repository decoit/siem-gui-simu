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

import de.decoit.siemgui.security.AuthorizedUser;
import java.security.Principal;
import org.springframework.security.core.Authentication;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public abstract class AbstractControllerHelper {
	protected final AuthorizedUser convertPrincipalToAuthorizedUser(Principal principal) {
		if(principal instanceof Authentication) {
			Authentication auth = (Authentication) principal;

			if(auth.getPrincipal() instanceof AuthorizedUser) {
				AuthorizedUser actUser = (AuthorizedUser) auth.getPrincipal();

				return actUser;
			}
			else {
				throw new IllegalArgumentException("Stored principal is not of type AuthorizedUser");
			}
		}
		else {
			throw new IllegalArgumentException("Provided principal is not of type Authentication");
		}
	}
}
