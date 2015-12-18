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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * A simple implementation of the UserAuthenticationService that forwards the operations directly to the DAO.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
	@Autowired
	UserDao userDao;


	@Override
	public User login(String username, String password) throws ExternalServiceException {
		User u = userDao.login(username, password);

		return u;
	}


	@Override
	public void logout(AuthorizedUser user) throws ExternalServiceException {
		userDao.logout(user);
	}
}
