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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.stereotype.Service;


/**
 * Utility class to convert between old school date API objects and the new Java 8 date API objects.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class DateConverter {
	/**
	 * Convert a Date object to a Java 8 LocalDateTime object.
	 *
	 * @param input The Date object
	 * @return The converted LocalDateTime object, null if input was null
	 */
	public LocalDateTime dateToLocalDateTime(Date input) {
		if (input != null) {
			// The date objects is similar to an Instant, thus apply local time zone to the Instant and convert that to LocalDateTime
			return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
		else {
			return null;
		}
	}


	/**
	 * Convert a Date object to a Java 8 LocalDateTime object.
	 * This method is able to convert Date objects that contain a GMT timestamp instead
	 * of a local time. Information stored in Date is always treated as local time, thus
	 * some additional work is required to get the correct LocalDateTime object.
	 *
	 * @param input The Date object with GMT time
	 * @return The converted LocalDateTime object, null if input was null
	 */
	public LocalDateTime gmtDateToLocalDateTime(Date input) {
		if (input != null) {
			// Date objects always represent time at local time zone. If the Date object contains a UTC time,
			// we have to do some juggling with time zones to get the correct local time after conversion.
			return input.toInstant().atZone(ZoneId.systemDefault()).withZoneSameLocal(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		}
		else {
			return null;
		}
	}


	/**
	 * Convert a LocalDateTime to an old school Date object.
	 *
	 * @param input The LocalDateTime object
	 * @return The converted Date object, null if input was null
	 */
	public Date localDateTimeToDate(LocalDateTime input) {
		if (input != null) {
			Instant instant = input.atZone(ZoneId.systemDefault()).toInstant();

			return Date.from(instant);
		}
		else {
			return null;
		}
	}
}
