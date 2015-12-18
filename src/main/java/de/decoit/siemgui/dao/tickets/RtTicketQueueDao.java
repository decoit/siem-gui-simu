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
package de.decoit.siemgui.dao.tickets;

import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
import de.decoit.siemgui.service.converter.RtTicketManagementConverter;
import de.decoit.rt.RtConnector;
import de.decoit.rt.RtException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Implementation of TicketQueueDao interface to access the ticket queues of a RT system using the RT Connector.
 * This class is currently not usable due to the fact that the session scope does not work for WebSocket connections.
 * The currently used class working around this issue is WorkaroundTicketQueueDao.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Repository
public class RtTicketQueueDao implements TicketQueueDao {
	private final Logger LOG = LoggerFactory.getLogger(RtTicketQueueDao.class.getName());

	@Autowired
	private RtConnector rtc;

	@Autowired
	private RtTicketManagementConverter rtObjConv;


	@Override
	public TicketQueue getTicketQueueDetails(long id, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				TicketQueue tq = rtObjConv.convertQueue(rtc.getQueue(authUser.getRtSessionId(), id));

				return tq;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting ticket queue details failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public TicketQueue getTicketQueueDetails(String name, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				TicketQueue tq = rtObjConv.convertQueue(rtc.getQueueByName(authUser.getRtSessionId(), name));

				return tq;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting ticket queue details failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public Map<Long, String> listQueues(AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				Map<Long, String> tqMap = rtc.listQueues(authUser.getRtSessionId());

				return tqMap;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting ticket queue list failed", ex);
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
