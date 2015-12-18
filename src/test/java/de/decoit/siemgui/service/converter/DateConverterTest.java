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

import de.decoit.siemgui.service.converter.DateConverter;
import java.time.LocalDateTime;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Test;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class DateConverterTest {

	/**
	 * Test of dateToLocalDateTime method, of class DateConverter.
	 */
	@Test
	public void testDateToLocalDateTime() {
		System.out.println("DateConverter.dateToLocalDateTime()");

		// 2014-08-07 12:30:15
		Date input = new Date(2014-1900, 7, 7, 12, 30, 15);
		DateConverter instance = new DateConverter();

		LocalDateTime result = instance.dateToLocalDateTime(input);

		assertEquals("Input and output years differ!", 2014, result.getYear());
		assertEquals("Input and output months differ!", 8, result.getMonthValue());
		assertEquals("Input and output days differ!", 7, result.getDayOfMonth());
		assertEquals("Input and output hours differ!", 12, result.getHour());
		assertEquals("Input and output minutes differ!", 30, result.getMinute());
		assertEquals("Input and output seconds differ!", 15, result.getSecond());
	}


	/**
	 * Test of localDateTimeToDate method, of class DateConverter.
	 */
	@Test
	public void testLocalDateTimeToDate() {
		System.out.println("DateConverter.localDateTimeToDate()");

		// 2014-08-07 12:30:15
		LocalDateTime input = LocalDateTime.of(2014, 8, 7, 12, 30, 15);
		DateConverter instance = new DateConverter();

		Date result = instance.localDateTimeToDate(input);

		assertEquals("Input and output years differ!", 2014-1900, result.getYear());
		assertEquals("Input and output months differ!", 8-1, result.getMonth());
		assertEquals("Input and output days differ!", 7, result.getDate());
		assertEquals("Input and output hours differ!", 12, result.getHours());
		assertEquals("Input and output minutes differ!", 30, result.getMinutes());
		assertEquals("Input and output seconds differ!", 15, result.getSeconds());
	}

}
