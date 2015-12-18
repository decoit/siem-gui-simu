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

import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.dao.tickets.filter.TicketFilter;
import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;


/**
 * Interface definition for accessing the tickets of the ticket system.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface TicketDao {
	/**
	 * Get an object with details about the ticket with the specified ID.
	 *
	 * @param id   ID of the ticket
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Ticket details object, null if an error occurred
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Ticket getTicketDetails(long id, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Search for tickets using the specified filter criteria and ordering definition.
	 *
	 * @param filter         Filter criteria for searching tickets
	 * @param orderByField   The field by which the results should be ordered
	 * @param orderDirection Ordering direction (ascending, descending)
	 * @param user           Current authorized user containing ticket system authentiation information
	 * @return Ordered list of Ticket objects
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<Ticket> searchTickets(TicketFilter filter, TicketFields orderByField, OrderByDirection orderDirection, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get the history of a specific ticket.
	 *
	 * @param id The ticket ID
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Ordered list of history items
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<TicketHistoryItem> getTicketHistory(long id, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Create a new ticket using the information included in the provided Ticket object.
	 * A possible ID value and any information of the Ticket object, which is not usable for ticket creation, will be ignored.
	 *
	 * @param ticket Ticket object holding the information
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return ID of the new ticket
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public long createTicket(NewTicket ticket, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Update a ticket with the information stored in the provided Ticket object.
	 * The ID value of the ticket object will be used for determining which ticket is going to be altered!
	 *
	 * @param ticket Ticket object holding the information
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return true if update was successful, false otherwise
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public boolean updateTicket(Ticket ticket, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Post a comment to the ticket with the specified ID.
	 * The meaning of a comment in comparision to and answer depends on the specific ticket system implementation.
	 *
	 * @param id      ID of the ticket to comment
	 * @param comment Comment text
	 * @param user    Current authorized user containing ticket system authentiation information
	 * @return true if comment posted was successful, false otherwise
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public boolean commentTicket(long id, String comment, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Post an answer to the ticket with the specified ID.
	 * The meaning of a comment in comparision to and answer depends on the specific ticket system implementation.
	 *
	 * @param id      ID of the ticket to answer
	 * @param comment Answer text
	 * @param user    Current authorized user containing ticket system authentiation information
	 * @return true if answer posted was successful, false otherwise
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public boolean answerTicket(long id, String comment, AuthorizedUser user) throws ExternalServiceException;
}
