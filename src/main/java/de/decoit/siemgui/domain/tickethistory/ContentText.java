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

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;


/**
 * This class holds the content text of a ticket history item.
 * It is made up of a list of ContentTextElement objects that contain the actual
 * content. By using the toString() method the list can be converted to a String
 * for output.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode
public class ContentText {
	private List<ContentTextElement> textElements;


	public ContentText() {
		textElements = new ArrayList<>();
	}


	/**
	 * Add a new element to the list.
	 *
	 * @param element The new content element
	 */
	public void addElement(ContentTextElement element) {
		textElements.add(element);
	}


	/**
	 * Convert the element list to a String.
	 * Each element is appended by a new line BB-code marker after calling its
	 * toString() method.
	 *
	 * @return Content text as a String
	 */
	@Override
	@JsonValue
	public String toString() {
		StringBuilder sb = new StringBuilder();

		boolean lastElementWasLine = false;
		for(ContentTextElement cte : textElements) {
			if(lastElementWasLine && cte instanceof ContentTextLine) {
				sb.append("[br]");
			}

			sb.append(cte.toString());
			if(cte instanceof ContentTextLine) {
				lastElementWasLine = true;
			}
			else {
				lastElementWasLine = false;
			}
		}

		return sb.toString();
	}
}
