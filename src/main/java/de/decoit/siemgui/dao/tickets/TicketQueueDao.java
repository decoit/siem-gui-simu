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
import java.util.Map;


/**
 * Interface definition for accessing the ticket queues of the ticket system.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface TicketQueueDao {
	/**
	 * Get an object with details about the ticket queue specified ID.
	 *
	 * @param id   ID of the ticket queue.
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Ticket queue details object, null if an error occurred
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public TicketQueue getTicketQueueDetails(long id, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get an object with details about the ticket queue specified name.
	 *
	 * @param name Name of the ticket queue.
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return Ticket queue details object, null if an error occurred
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public TicketQueue getTicketQueueDetails(String name, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Get a 'ID =&gt; Name' mapping for all ticket queues available in the ticket system.
	 *
	 * @param user Current authorized user containing ticket system authentiation information
	 * @return A map conatining the above information, null if an error occurred
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Map<Long, String> listQueues(AuthorizedUser user) throws ExternalServiceException;
}
