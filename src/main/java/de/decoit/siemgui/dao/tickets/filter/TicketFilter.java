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
package de.decoit.siemgui.dao.tickets.filter;

import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.User;
import java.time.LocalDateTime;


/**
 * Construct a collection of filter criteria to search for specific tickets.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public interface TicketFilter {
	/**
	 * Use a logical AND to link the next condition in the chain.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter and();


	/**
	 * Use a logical OR to link the next condition in the chain.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter or();


	/**
	 * Add an opening parenthesis to the query. Parenthesis are not automatically closed,
	 * this must be done by the caller using rightParenthesis().
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter leftParenthesis();


	/**
	 * Add an closing parenthesis to the query.
	 *
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter rightParenthesis();


	/**
	 * Add a filter condition that the ID has to match a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter idIs(long value);


	/**
	 * Add a filter condition that the ID is not allowed to match a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter idIsNot(long value);


	/**
	 * Add a filter condition that ID must be less than a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter idLessThan(long value);


	/**
	 * Add a filter condition that ID must be greater than a specific value.
	 *
	 * @param value The value to compare ID to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter idGreaterThan(long value);


	/**
	 * Add a filter condition that the ticket queue name must match a specific value.
	 *
	 * @param value The value to compare queue to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter queueIs(String value);


	/**
	 * Add a filter condition that the ticket queue name must not match a specific value.
	 *
	 * @param value The value to compare queue to
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter queueIsNot(String value);


	/**
	 * Add a filter condition that the subject must contain a specific phrase.
	 *
	 * @param value Substring that should be present in the subject
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter subjectContains(String value);


	/**
	 * Add a filter condition that the subject must not contain a specific phrase.
	 *
	 * @param value Substring that should not be present in the subject
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter subjectDoesNotContain(String value);


	/**
	 * Add a filter condition that the owner equals a specific user.
	 *
	 * @param value User object which will be used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter ownerIs(User value);


	/**
	 * Add a filter condition that the owner must not equal a specific user.
	 *
	 * @param value User object which will be used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter ownerIsNot(User value);


	/**
	 * Add a filter condition that the status must be equal to a specific status.
	 *
	 * @param value Status used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter statusIs(TicketStatus value);


	/**
	 * Add a filter condition that the status must not be equal to a specific status.
	 *
	 * @param value Status used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter statusIsNot(TicketStatus value);


	/**
	 * Add a filter condition that the created on date must match a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter createdOnIs(LocalDateTime value);


	/**
	 * Add a filter condition that the created on date must be less ('earlier') than a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter createdOnLessThan(LocalDateTime value);


	/**
	 * Add a filter condition that the created on date must be greater ('later') than a specific date.
	 *
	 * @param value Date used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter createdOnGreaterThan(LocalDateTime value);


	/**
	 * Add a filter condition that the time worked must be equal to a specific time (in minutes).
	 *
	 * @param value Number of minutes used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter timeWorkedIs(int value);


	/**
	 * Add a filter condition that the time worked must not be equal to a specific time (in minutes).
	 *
	 * @param value Number of minutes used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter timeWorkedIsNot(int value);


	/**
	 * Add a filter condition that the time worked must be less than a specific time (in minutes).
	 *
	 * @param value Number of minutes used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter timeWorkedLessThan(int value);


	/**
	 * Add a filter condition that the time worked must be greater than a specific time (in minutes).
	 *
	 * @param value Number of minutes used for comparision
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter timeWorkedGreaterThan(int value);


	/**
	 * Add a filter condition that the priority must match a specific value.
	 *
	 * @param value Value to compare the priority with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter priorityIs(int value);


	/**
	 * Add a filter condition that the priority must not match a specific value.
	 *
	 * @param value Value to compare the priority with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter priorityIsNot(int value);


	/**
	 * Add a filter condition that the priority must be less than a specific value.
	 *
	 * @param value Value to compare the priority with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter priorityLessThan(int value);


	/**
	 * Add a filter condition that the priority must be greater than a specific value.
	 *
	 * @param value Value to compare the priority with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter priorityGreaterThan(int value);


	/**
	 * Add a filter condition that the associated incident's ID must match a specific value.
	 *
	 * @param value Value to compare incident ID with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter incidentIs(long value);


	/**
	 * Add a filter condition that the associated incident's risk must match a specific value.
	 *
	 * @param value Value to compare incident risk with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter riskIs(int value);


	/**
	 * Add a filter condition that the associated incident's risk must not match a specific value.
	 *
	 * @param value Value to compare incident risk with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter riskIsNot(int value);


	/**
	 * Add a filter condition that the associated incident's risk must be less than a specific value.
	 *
	 * @param value Value to compare incident risk with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter riskLessThan(int value);


	/**
	 * Add a filter condition that the associated incident's risk must be greater than a specific value.
	 *
	 * @param value Value to compare incident risk with
	 * @return The filter object (this) to add further filter conditions
	 */
	public TicketFilter riskGreaterThan(int value);


	/**
	 * Convert the filter into the native format used by the filtering service.
	 * Return type depends on the implementation.
	 *
	 * @return Native filter format, type depends on implementation
	 */
	public Object toNativeFilter();
}
