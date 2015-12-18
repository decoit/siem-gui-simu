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

import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import java.util.List;
import java.util.Map;


/**
 * An interface to define a service that reads data from and writes to an incident database.
 * The incidents should be fetched by a specific event, for example a scheduler, and then stored
 * in a cache inside the service for faster access and less load on the incident database.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface IncidentService {
	/**
	 * Fetch the incident with the specified ID from the cached list of incidents.
	 *
	 * @param id Incident ID
	 * @return Fetched incident
	 */
	public Incident getIncident(long id);


	/**
	 * Get statistics about the incidents found in the cached list of incidents.
	 * This data is used by the overview webpage. It must contain categories labeled with strings:<br>
	 * - Number of incidents with status NEW<br>
	 * - Number of incidents with status IN_PROGRESS<br>
	 * - Number of incidents with status UNKNOWN<br>
	 * - Number of incidents with threat level LOW<br>
	 * - Number of incidents with threat level MEDIUM<br>
	 * - Number of incidents with threat level HIGH
	 *
	 * @return Statistics packaged in a map
	 */
	public Map<String, Long> countIncidentsForOverview();


	/**
	 * Count incidents grouped by threat level.
	 *
	 * @return A map with number of incidents per threat level
	 */
	public Map<ThreatLevel, Long> calculateThreatLevel();


	/**
	 * Fetch a list of active incidents from the cached list of incidentse.
	 * Active incident means that it must have a status different from DONE.
	 *
	 * @return List of incidents
	 */
	public List<Incident> getActiveIncidents();


	/**
	 * Fetch a list of resolved incidents from the cached list of incidents.
	 * Active incident means that it must have the status DONE.
	 *
	 * @return List of incidents
	 */
	public List<Incident> getResolvedIncidents();


	public List<TicketHistoryItem> getHistoryForIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Assign an incident to the currently active user.
	 * The incident is marked as takes by that user in the ticket system.
	 *
	 * @param incidentId ID of the incident to take
	 * @param user       Authorization information required to access the ticket system
	 *
	 * @throws ExternalServiceException if contacting the ticket system failed
	 */
	public void takeIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Begin work on the specified incident.
	 * The incident is marked as started in the ticket system. Starting an incident
	 * is only possible if it is owned by the currently active user.
	 *
	 * @param incidentId ID of the incident to begin with
	 * @param user       Authorization information required to access the ticket system
	 *
	 * @throws ExternalServiceException if contacting the ticket system failed
	 */
	public void beginWorkOnIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Finish work on the specified incident.
	 * The incident is marked as finished in the ticket system. Finishing an incident
	 * is only possible if it is owned by the currently active user.
	 *
	 * @param incidentId ID of the incident to finish
	 * @param user       Authorization information required to access the ticket system
	 *
	 * @throws ExternalServiceException if contacting the ticket system failed
	 */
	public void finishWorkOnIncident(long incidentId, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Post a comment on the specified incident.
	 * The comment is posted in the ticket backing the incident. The content text is parsed into a
	 * ContentText object structure to format the text before storing. This speeds up loading of the
	 * history because it is not required to parse and format the string that is returned by the
	 * ticket system.
	 *
	 * @param incidentId ID of the incident to comment on
	 * @param commentText Content of the comment as provided by the frontend
	 * @param user Authorization information required to access the ticket system
	 * @return true if comment posted was successful, false otherwise
	 * @throws ExternalServiceException if contacting the ticket system failed
	 */
	public boolean postCommentOnIncident(long incidentId, String commentText, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Book time worked on an incident.
	 * The time must be provided in minutes and will be added to the current value of time worked
	 * that is stored in the incident's ticket.
	 *
	 * @param incidentId ID of the incident to book time on
	 * @param minutes Minutes to book on the ticket
	 * @param user Authorization information required to access the ticket system
	 * @return New total amount of minutes worked on this incident
	 * @throws ExternalServiceException if contacting the ticket system failed
	 */
	public int bookTimeOnIncident(long incidentId, int minutes, AuthorizedUser user) throws ExternalServiceException;


	/**
	 * Lookup incidents from the incident database and store them into the internal cache.
	 */
	public void incidentLookup();
}
