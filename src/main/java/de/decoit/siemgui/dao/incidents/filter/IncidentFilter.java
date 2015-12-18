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
package de.decoit.siemgui.dao.incidents.filter;

import de.decoit.siemgui.domain.IncidentStatus;
import java.time.LocalDateTime;


/**
 * Construct a collection of filter criteria to search for specific incidents.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface IncidentFilter {
	/**
	 * Use a logical AND to link the next condition in the chain.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter and();


	/**
	 * Use a logical OR to link the next condition in the chain.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter or();


	/**
	 * Add an opening parenthesis to the query. Parenthesis are not automatically closed,
	 * this must be done by the caller using rightParenthesis().
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter leftParenthesis();


	/**
	 * Add an closing parenthesis to the query.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter rightParenthesis();


	/**
	 * Add a filter condition that the ID has to match a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter idIs(long value);


	/**
	 * Add a filter condition that the ID is not allowed to match a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter idIsNot(long value);


	/**
	 * Add a filter condition that ID must be less than a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter idLessThan(long value);


	/**
	 * Add a filter condition that ID must be greater than a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter idGreaterThan(long value);


	/**
	 * Add a filter condition that the status must be equal to a specific status.
	 *
	 * @param value Status used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter statusIs(IncidentStatus value);


	/**
	 * Add a filter condition that the status must not be equal to a specific status.
	 *
	 * @param value Status used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter statusIsNot(IncidentStatus value);


	/**
	 * Add a filter condition that the created on date must match a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter createdOnIs(LocalDateTime value);


	/**
	 * Add a filter condition that the created on date must be less ('earlier') than a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter createdOnLessThan(LocalDateTime value);


	/**
	 * Add a filter condition that the created on date must be greater ('later') than a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter createdOnGreaterThan(LocalDateTime value);


	/**
	 * Add a filter condition that the risk must match a specific value.
	 *
	 * @param value The value to compare risk to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter riskIs(int value);


	/**
	 * Add a filter condition that the risk must not match a specific value.
	 *
	 * @param value The value to compare risk to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter riskIsNot(int value);


	/**
	 * Add a filter condition that the risk must be less than a specific value.
	 *
	 * @param value The value to compare risk to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter riskLessThan(int value);


	/**
	 * Add a filter condition that the risk must be greater than a specific value.
	 *
	 * @param value The value to compare risk to
	 * @return The filter object (this) to add further filter conditions
	 */
	public IncidentFilter riskGreaterThan(int value);


	/**
	 * Convert the filter into the native format used by the filtering service.
	 * Return type depends on the implementation.
	 *
	 * @return Native filter format, type depends on implementation
	 */
	public Object toNativeFilter();
}
