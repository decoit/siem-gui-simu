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
package de.decoit.siemgui.exception;


/**
 * This exception is thrown if converter services cannot convert an object.
 * A message telling the caller what failed is mandatory, if caused by an other exception
 * the cause can be attached aswell.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class ConversionException extends RuntimeException {
	public ConversionException(String msg) {
		super(msg);
	}


	public ConversionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
