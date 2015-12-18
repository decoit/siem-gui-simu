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

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ConversionException;
import java.util.List;


/**
 * This interface has to be implemented by all classed that realize conversion from and to user management domain objects.
 * It is defined as a generic interface to leave definition of correlation implementation specific types to the implementing class.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <USER> Implementation specific user type
 */
public interface UserManagementConverter<USER> {
	/**
	 * Convert an implementation specific user object into a general User domain object.
	 *
	 * @param usr Input object
	 * @return Created domain object
	 *
	 * @throws ConversionException if an error occurs during conversion
	 */
	public User convertUser(USER usr) throws ConversionException;


	/**
	 * Convert a list of implementation specific user objects into a list of general User domain objects.
	 *
	 * @param usrList Input list
	 * @return Created list of domain objects
	 * @throws ConversionException if an error occurs during conversion
	 */
	public List<User> convertUserList(List<USER> usrList) throws ConversionException;


	/**
	 * Convert a general domain User object into an implementation specific user object.
	 *
	 * @param user Input domain object
	 * @return Created implementation specific object
	 * @throws ConversionException if an error occurs during conversion
	 */
	public USER convertDomUser(User user) throws ConversionException;
}
