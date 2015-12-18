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

import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.tickethistory.ContentText;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;


/**
 * Interface definition for implementing a service that allows access to the ticket part of the DAO layer.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface TicketService {
	/**
	 * Get all tickets that are associated with the SIEM system from the ticket system.
	 *
	 * @param orderByField     Order the received tickets by this field's value
	 * @param orderByDirection Order the fields value in this direction
	 * @param user             Current authorized user containing ticket system authentiation information
	 * @return Ordered list of tickets
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<Ticket> getAllSiemTickets(TicketFields orderByField, OrderByDirection orderByDirection, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get all tickets that are associated with the SIEM system and have a specific status from the ticket system.
	 *
	 * @param status           Only tickets with this status are to be retrieved
	 * @param orderByField     Order the received tickets by this field's value
	 * @param orderByDirection Order the fields value in this direction
	 * @param user             Current authorized user containing ticket system authentiation information
	 * @return Ordered list of tickets
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<Ticket> getSiemTicketsWithStatus(TicketStatus status, TicketFields orderByField, OrderByDirection orderByDirection, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get the ticket that is associated with a specific incident.
	 * If not ticket is assigned to this incident, a new one has to be created and returned.
	 *
	 * @param inc Incident that the ticket has to be associated with
	 * @return The fetched or created ticket
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Ticket getTicketForIncident(Incident inc) throws ExternalServiceException;


	/**
	 * Fetch the history of a specific ticket.
	 *
	 * @param ticket The ticket
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Ordered list of history items
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<TicketHistoryItem> getTicketHistory(Ticket ticket, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Update the information stored in a specific ticket.
	 *
	 * @param ticket Ticket object containing the new information
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return The updated ticket object
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Ticket updateTicket(Ticket ticket, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Fetch a specific ticket queue from the ticket system.
	 *
	 * @param queueName Name of the queue
	 * @param user      Current authorized user containing ticket system authentiation information
	 * @return Object containing the queue details
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public TicketQueue getTicketQueueDetails(String queueName, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Post a comment on a specific ticket.
	 *
	 * @param ticket The ticket to comment on
	 * @param comment The content of the comment
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return true if comment posted was successful, false otherwise
	 * @throws ExternalServiceException
	 */
	public boolean postTicketComment(Ticket ticket, ContentText comment, AuthorizedUser user) throws ExternalServiceException;
}
