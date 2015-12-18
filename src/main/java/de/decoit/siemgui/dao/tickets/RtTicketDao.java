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

import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.filter.RtTicketFilter;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.dao.tickets.filter.TicketFilter;
import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
import de.decoit.siemgui.service.converter.RtTicketManagementConverter;
import de.decoit.rt.RtConnector;
import de.decoit.rt.RtException;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicketHistoryItem;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Implementation of TicketDao interface to access the tickets of a RT system using the RT Connector.
 * This class is currently not usable due to the fact that the session scope does not work for WebSocket connections.
 * The currently used class working around this issue is WorkaroundTicketQueueDao.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Repository
public class RtTicketDao implements TicketDao {
	private final Logger LOG = LoggerFactory.getLogger(RtTicketDao.class.getName());

	@Autowired
	private RtConnector rtc;

	@Autowired
	private RtTicketManagementConverter rtObjConv;

	@Autowired
	private SystemConfig sysConf;


	@Override
	public boolean answerTicket(long id, String comment, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			RtTicketHistoryItem rthist = new RtTicketHistoryItem();

			rthist.setTicketId(id);
			rthist.setType(RtTicketHistoryItem.RtTicketHistoryItemType.CORRESPOND);
			rthist.setContent(comment);

			try {
				boolean success = rtc.answerTicket(authUser.getRtSessionId(), id, rthist);

				return success;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Posting ticket answer failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public boolean commentTicket(long id, String comment, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if(LOG.isDebugEnabled()) {
			LOG.debug("commentTicket, called with [id:" + id + " comment:" + comment + " user:" + user.toString());
		}

		if (authUser.isAuthenticated()) {
			RtTicketHistoryItem rthist = new RtTicketHistoryItem();

			rthist.setTicketId(id);
			rthist.setType(RtTicketHistoryItem.RtTicketHistoryItemType.COMMENT);
			rthist.setContent(comment);

			try {
				boolean success = rtc.commentTicket(authUser.getRtSessionId(), id, rthist);

				return success;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Posting ticket comment failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public long createTicket(NewTicket ticket, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			RtTicket rtt = rtObjConv.convertDomNewTicket(ticket);

			try {
				long newId = rtc.createTicket(authUser.getRtSessionId(), rtt);

				return newId;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Creating ticket failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public Ticket getTicketDetails(long id, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			try {
				Ticket ticket = rtObjConv.convertTicket(rtc.getTicket(authUser.getRtSessionId(), id), user);

				return ticket;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Getting ticket details failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public List<TicketHistoryItem> getTicketHistory(long id, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if(authUser.isAuthenticated()) {
			try {
				List<TicketHistoryItem> resultList = rtObjConv.convertTicketHistoryList(rtc.getTicketHistory(authUser.getRtSessionId(), id), user);

				return resultList;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Fetching ticket history failed.", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public List<Ticket> searchTickets(TicketFilter filter, TicketFields orderByField, OrderByDirection orderDirection, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			// Get filter string from filter object, only RtTicketFilter instances are allowed
			String filterStr;
			if (filter instanceof RtTicketFilter) {
				filterStr = (String) filter.toNativeFilter();

				if (LOG.isDebugEnabled()) {
					LOG.debug(filterStr);
				}
			}
			else {
				throw new IllegalArgumentException("Call with a filter that is not an instance of RtTicketFilter is not supported");
			}

			// Create the order by string from the additional arguments
			StringBuilder orderSb = new StringBuilder();

			if (orderDirection == OrderByDirection.DESCENDING) {
				// Descending ordering need the "-" prefix
				orderSb.append("-");
			}

			switch (orderByField) {
				case ID:
					orderSb.append("id");
					break;
				case SUBJECT:
					orderSb.append("Subject");
					break;
				case OWNER:
					orderSb.append("Owner");
					break;
				case STATUS:
					orderSb.append("Status");
					break;
				case CREATED_ON:
					orderSb.append("Created");
					break;
				case TIME_WORKED:
					orderSb.append("TimeWorked");
					break;
				case PRIORITY:
					orderSb.append("Priority");
					break;
				case INCIDENT:
					orderSb.append("CF.{");
					orderSb.append(sysConf.getIncidentIdCustomField());
					orderSb.append("}");
					break;
				case RISK:
					orderSb.append("CF.{");
					orderSb.append(sysConf.getIncidentRiskCustomField());
					orderSb.append("}");
					break;
				default:
					throw new IllegalArgumentException("Invalid value for orderByField detected: " + orderByField);
			}

			try {
				// Execute the query to RT
				List<Ticket> list = rtObjConv.convertTicketList(rtc.searchTickets(authUser.getRtSessionId(), filterStr, orderSb.toString()), user);

				return list;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Searching for tickets failed", ex);
			}
		}
		else {
			throw new IllegalStateException("Provided user is not authenticated");
		}
	}


	@Override
	public boolean updateTicket(Ticket ticket, AuthorizedUser user) throws ExternalServiceException {
		RtAuthorizedUser authUser = convertAuthorizedUser(user);

		if (authUser.isAuthenticated()) {
			RtTicket rtt = rtObjConv.convertDomTicket(ticket);

			try {
				boolean success = rtc.editTicket(authUser.getRtSessionId(), rtt);

				return success;
			}
			catch (RtException ex) {
				throw new ExternalServiceException("Updating ticket failed", ex);
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
