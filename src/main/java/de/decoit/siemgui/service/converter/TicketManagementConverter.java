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
package de.decoit.siemgui.service.converter;

import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.exception.ConversionException;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;


/**
 * This interface has to be implemented by all classed that realize conversion from and to ticket management domain objects.
 * It is defined as a generic interface to leave definition of correlation implementation specific types to the implementing class.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <TICKET>  Implementation specific ticket type
 * @param <QUEUE>   Implementation specific ticket queue type
 * @param <HISTORY> Implementation specific ticket history item type
 */
public interface TicketManagementConverter<TICKET, QUEUE, HISTORY> {
	/**
	 * Convert an implementation specific ticket object into a general Ticket domain object.
	 *
	 * @param ticket Input object
	 * @param user   Current authorized user containing ticket system authentiation information
	 * @return The created domain object
	 *
	 * @throws ConversionException      if an error occurs during conversion
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Ticket convertTicket(TICKET ticket, AuthorizedUser user) throws ConversionException, ExternalServiceException;


	/**
	 * Convert a list of implementation specific ticket object into a list of general Ticket domain objects.
	 *
	 * @param ticketList Input object list
	 * @param user       Current authorized user containing ticket system authentiation information
	 * @return The created domain object list
	 *
	 * @throws ConversionException      if an error occurs during conversion
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<Ticket> convertTicketList(List<TICKET> ticketList, AuthorizedUser user) throws ConversionException, ExternalServiceException;


	/**
	 * Convert a general Ticket domain object into an implementation specific ticket object.
	 *
	 * @param ticket Input object
	 * @return The created implementation specific object
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public TICKET convertDomTicket(Ticket ticket) throws ConversionException;


	/**
	 * Convert a list of general Ticket domain object into a list of implementation specific ticket object.
	 *
	 * @param ticketList Input object list
	 * @return The created list of implementation specific objects
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public List<TICKET> convertDomTicketList(List<Ticket> ticketList) throws ConversionException;


	/**
	 * Convert a NewTicket domain object into an implementation specific ticket object.
	 * The generated ticket object should contain all information required to create a new ticket inside the
	 * ticket system.
	 *
	 * @param ticket Input object
	 * @return The created implementation specific object
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public TICKET convertDomNewTicket(NewTicket ticket) throws ConversionException;


	/**
	 * Convert a list of NewTicket domain object into a list of implementation specific ticket object.
	 * The generated ticket objects should contain all information required to create new tickets inside the
	 * ticket system.
	 *
	 * @param ticketList Input object list
	 * @return The created list of implementation specific objects
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public List<TICKET> convertDomNewTicketList(List<NewTicket> ticketList) throws ConversionException;


	/**
	 * Convert an implementation specific ticket history item object into a general TicketHistoryItem domain object.
	 *
	 * @param historyItem Input object
	 * @param user        Current authorized user containing ticket system authentiation information
	 * @return The created domain object
	 *
	 * @throws ConversionException      if an error occurs during conversion
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public TicketHistoryItem convertTicketHistory(HISTORY historyItem, AuthorizedUser user) throws ConversionException, ExternalServiceException;


	/**
	 * Convert a list of implementation specific ticket history item object into a list of general TicketHistoryItem domain objects.
	 *
	 * @param historyItemList Input object list
	 * @param user            Current authorized user containing ticket system authentiation information
	 * @return The created domain object list
	 *
	 * @throws ConversionException      if an error occurs during conversion
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<TicketHistoryItem> convertTicketHistoryList(List<HISTORY> historyItemList, AuthorizedUser user) throws ConversionException, ExternalServiceException;


	/**
	 * Convert an implementation specific queue object into a general Queue domain object.
	 *
	 * @param queue Input object
	 * @return The created domain object
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public TicketQueue convertQueue(QUEUE queue) throws ConversionException;


	/**
	 * Convert a list of implementation specific queue object into a list of general Queue domain object.
	 *
	 * @param queueList Input object list
	 * @return The created domain object list
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public List<TicketQueue> convertQueueList(List<QUEUE> queueList) throws ConversionException;
}
