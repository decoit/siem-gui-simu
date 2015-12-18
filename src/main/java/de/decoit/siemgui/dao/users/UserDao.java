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
package de.decoit.siemgui.dao.users;

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;


/**
 * Interface definition for accessing the user management system.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface UserDao {
	/**
	 * Get details about the user with the specified username.
	 *
	 * @param uname Username to use
	 * @param user  Current authorized user containing user management system authentiation information
	 * @return Object of the requested user, null if username does not exist
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User getUserDetails(String uname, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get details about the user with the specified username.
	 *
	 * @param uid User ID to use
	 * @param user  Current authorized user containing user management system authentiation information
	 * @return Object of the requested user, null if username does not exist
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User getUserDetails(long uid, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get a list of all users registered in the user management system.
	 * The list is ordered by user name, order direction is ascending.
	 *
	 * @param user Current authorized user containing user management system authentiation information
	 * @return Ordered list of user details
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<User> getUserList(AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Update a user with the information stored in the provided User object.
	 * The ID value of the user object will be used for determining which user is going to be altered!
	 *
	 * @param updUser User object holding the information
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return true if update was successful, false otherwise
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public boolean updateUser(User updUser, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Login with the provided credentials.
	 *
	 * @param uname Username to use
	 * @param pwd   Password for username
	 * @return Object of the logged in user, null if login failed
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public User login(String uname, String pwd) throws ExternalServiceException;


	/**
	 * Logout the current user.
	 *
	 * @param user Current authorized user containing user management system authentiation information
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public void logout(AuthorizedUser user) throws ExternalServiceException;
}
