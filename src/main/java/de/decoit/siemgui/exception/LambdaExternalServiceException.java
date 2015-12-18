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
 * This is an unchecked wrapper exception for the ExternalServiceException.
 * Checked exceptions like the ExternalServiceException cannot be caught in lambda
 * expression and thus methods throwing these cannot be used in labda expressions.
 * To circumvent that issue the method can be wrapped in an additional method that
 * catches the ExternalServiceException and wraps it into this exception. The caller
 * then has to catch the LambdaExternalServiceException, unpack the original exception
 * and process it.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class LambdaExternalServiceException extends RuntimeException {
	public LambdaExternalServiceException(ExternalServiceException cause) {
		super(cause);
	}
}
