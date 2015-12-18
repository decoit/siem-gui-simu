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
package de.decoit.siemgui.domain.tickethistory;

import lombok.EqualsAndHashCode;



/**
 * This subclass of ContentTextElement represents a single line of text.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode(callSuper = false)
public class ContentTextLine extends ContentTextElement {
	private final String line;


	/**
	 * Construct a new object containing the provided line of text.
	 *
	 * @param line Line of text to be held by this object
	 */
	public ContentTextLine(String line) {
		this.line = line;
	}


	/**
	 * Appends the line provided during construction with a new line BB-code marker and returns the result.
	 *
	 * @return Line of text appended by a new line character
	 */
	@Override
	public String toString() {
		return line;
	}
}
