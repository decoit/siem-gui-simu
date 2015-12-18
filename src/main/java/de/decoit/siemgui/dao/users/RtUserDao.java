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
import de.decoit.siemgui.security.RtAuthorizedUser;
import de.decoit.siemgui.service.converter.RtUserManagementConverter;
import de.decoit.rt.RtConnector;
import de.decoit.rt.RtException;
import de.decoit.rt.model.RtUser;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Implementation of UserDao to use the RT system as a user management.
 * The system is accessed using the RT Connector.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Repository
public class RtUserDao implements UserDao {
	private final Logger LOG = LoggerFactory.getLogger(RtUserDao.class.getName());

	@Autowired
	private RtConnector rtc;

	@Autowired
	private RtUserManagementConverter rtObjConv;


	@Override
	public User getUserDetails(String uname, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				User u = rtObjConv.convertUser(rtc.getUser(authUser.getRtSessionId(), uname));

				return u;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting user details failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public User getUserDetails(long uid, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				User u = rtObjConv.convertUser(rtc.getUser(authUser.getRtSessionId(), uid));

				return u;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting user details failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public List<User> getUserList(AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				List<User> userList = rtObjConv.convertUserList(rtc.searchUsers(authUser.getRtSessionId(), "", "Name"));

				return userList;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting user list failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public boolean updateUser(User updUser, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			RtUser rtu = rtObjConv.convertDomUser(updUser);

			try {
				boolean success = rtc.editUser(authUser.getRtSessionId(), rtu);

				return success;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Updating user failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public User login(String uname, String pwd) throws ExternalServiceException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("SSL KeyStore: " + System.getProperty("javax.net.ssl.keyStore"));
			LOG.debug("SSL TrustStore: " + System.getProperty("javax.net.ssl.trustStore"));
		}

		try {
			String sessionId = rtc.login(uname, pwd);

			User actUser = rtObjConv.convertUser(rtc.getUser(sessionId, uname));
			actUser.setSessionId(sessionId);

			return actUser;
		}
		catch (RtException ex) {
			throw new ExternalServiceException("Logging in to RT failed", ex);
		}
	}


	@Override
	public void logout(AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				rtc.logout(authUser.getRtSessionId());

				// Invalidate and remove any authentication information
				authUser.setRtSessionId(null);
				authUser.setUsername(null);
				authUser.setAuthenticated(false);
				authUser.setLastAnsweredHeartbeat(null);
				authUser.setAlive(false);
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Logout from RT failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	/**
	 * Cast an AuthorizedUser object into a RtAuthorizedUser object for use in this class.
	 * Will only work for actual instances of RtAuthorizedUser, any other object will cause
	 * an IllegalArgumentException exception.
	 *
	 * @param user The AuthorizedUser object provided via the interface
	 * @return The cast RtAuthorizedUser object
	 */
	private RtAuthorizedUser convertAuthorizedUser(AuthorizedUser user) {
		if (user instanceof RtAuthorizedUser) {
			return (RtAuthorizedUser) user;
		}
		else {
			throw new IllegalArgumentException("User authentication object was no instance of RtAuthorizedUser");
		}
	}
}
