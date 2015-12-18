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

import de.decoit.siemgui.dao.users.UserDao;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * A simple implementation of the UserDetailsService that forwards the operations directly to the DAO.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserDao userDao;


	@Override
	public User getUserDetails(String username, AuthorizedUser user) throws ExternalServiceException {
		User u = userDao.getUserDetails(username, user);

		return u;
	}


	@Override
	public User getUserDetailsById(long uid, AuthorizedUser user) throws ExternalServiceException {
		User u = userDao.getUserDetails(uid, user);

		return u;
	}


	@Override
	public List<User> getAllUserDetails(AuthorizedUser user) throws ExternalServiceException {
		List<User> userList = userDao.getUserList(user);

		return userList;
	}


	@Override
	public User updateUser(User updUser, AuthorizedUser user) throws ExternalServiceException {
		boolean success = userDao.updateUser(updUser, user);

		if(!success) {
			throw new ExternalServiceException("Update user task returned false");
		}

		User newUser = userDao.getUserDetails(updUser.getId(), user);

		return newUser;
	}
}
