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

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;


/**
 * This subclass of ContentTextElement represents a quoted text.
 * It contains a list of ContentTextElement objects, just like the ContentText class.
 * Quotes can contain additional quotes and thus any text element can be added to this
 * class.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@EqualsAndHashCode(callSuper = false)
public class ContentTextQuote extends ContentTextElement {
	private List<ContentTextElement> textElements;


	public ContentTextQuote() {
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
	public String toString() {
		StringBuilder sb = new StringBuilder("[bq]");

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

		sb.append("[/bq]");

		return sb.toString();
	}
}
