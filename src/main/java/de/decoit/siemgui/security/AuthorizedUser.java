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

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * This is an abstract base class for authentication information that will be used as a Principal in Spring Security.
 * It stores the username of the athenticated user and the last heartbeat ID this user answered. Additionally it
 * defines that flags authenticated and alive. First one meaning the user's authentication was successful, the second
 * one is used to mark a user as dead if he did not answer heartbeats for some time.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString(exclude = { "lastAnsweredHeartbeat" })
@EqualsAndHashCode(exclude = { "lastAnsweredHeartbeat" })
public abstract class AuthorizedUser implements Serializable {
	protected String username;
	protected boolean authenticated = false;
	protected String lastAnsweredHeartbeat;
	protected boolean alive = true;
}
