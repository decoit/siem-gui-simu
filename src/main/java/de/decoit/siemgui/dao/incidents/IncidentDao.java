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
package de.decoit.siemgui.dao.incidents;

import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.incidents.filter.IncidentFields;
import de.decoit.siemgui.dao.incidents.filter.IncidentFilter;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.exception.ExternalServiceException;
import java.util.List;


/**
 * Interface definition for accessing the incident database.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface IncidentDao {
	/**
	 * Return the incident with the specified ID number.
	 * If no such incident exists, the method will return null.
	 *
	 * @param id Incident ID
	 * @return Incident object or null
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Incident getIncident(long id) throws ExternalServiceException;


	public List<Incident> getAllIncidents() throws ExternalServiceException;


	public List<Incident> getNewIncidents(long lastId) throws ExternalServiceException;


	/**
	 * Search for incidents using the specified filter criteria and ordering definition.
	 *
	 * @param filter         Filter criteria for searching incidents
	 * @param orderByField   The field by which the results should be ordered
	 * @param orderDirection Ordering direction (ascending, descending)
	 * @return Ordered list of Incident objects
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public List<Incident> searchIncidents(IncidentFilter filter, IncidentFields orderByField, OrderByDirection orderDirection) throws ExternalServiceException;


	/**
	 * Update the specified incident in the persistence layer and return an Incident object with the updated information.
	 * The ID of the incident to update will be the ID of the provided Incident object.
	 *
	 * @param inInc Object containing the new properties for the incident
	 * @return A new Incident object filled with the updated information
	 *
	 * @throws ExternalServiceException if an error occurs while accessing the external service
	 */
	public Incident updateIncident(Incident inInc) throws ExternalServiceException;
}
