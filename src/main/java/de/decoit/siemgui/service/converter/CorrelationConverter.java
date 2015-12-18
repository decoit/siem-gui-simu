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

import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentRecommendation;
import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.exception.ConversionException;
import java.util.List;


/**
 * This interface has to be implemented by all classed that realize conversion from and to correlation domain objects.
 * It is defined as a generic interface to leave definition of correlation implementation specific types to the implementing class.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <INCIDENT> Implementation specific incident type
 * @param <STATUS>   Implementation specific incident status type
 */
public interface CorrelationConverter<INCIDENT, STATUS> {
	/**
	 * Convert an implementation specific incident object to a general Incident domain object.
	 *
	 * @param inc Input object
	 * @return Created domain object
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public Incident convertIncident(INCIDENT inc) throws ConversionException;


	public List<Incident> convertIncidentList(List<INCIDENT> incList) throws ConversionException;


	/**
	 * Convert a general IncidentStatus enum to an implementation specific enum.
	 *
	 * @param status Input enum
	 * @return Converted enum
	 */
	public STATUS convertDomIncidentStatus(IncidentStatus status);
}
