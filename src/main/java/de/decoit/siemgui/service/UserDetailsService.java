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
package de.decoit.siemgui.service;

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;


/**
 * Interface definition of a service to retrieve and change information about users using the user part of the DAO layer.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface UserDetailsService {
	/**
	 * Get details about the user identified by the provided username.
	 *
	 * @param username Username to look for
	 * @param user     Current authorized user containing user management system authentiation information
	 * @return User object containing the details
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User getUserDetails(String username, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get details about the user identified by the provided user ID.
	 *
	 * @param uid User ID to look for
	 * @param user     Current authorized user containing user management system authentiation information
	 * @return User object containing the details
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User getUserDetailsById(long uid, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get a list of all users registered in the user management system.
	 * The list is ordered by user name, order direction is ascending.
	 *
	 * @param user Current authorized user containing user management system authentiation information
	 * @return Ordered list of user details
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<User> getAllUserDetails(AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Update the information stored for a specific user.
	 *
	 * @param updUser User object containing the new information
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return The updated user object
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User updateUser(User updUser, AuthorizedUser user) throws ExternalServiceException;
}
