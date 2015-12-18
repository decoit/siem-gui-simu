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
package de.decoit.siemgui.domain.charts;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Axis types that can be used with the Highcharts chart drawing library.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public enum AxisType {
	LINEAR,
	LOGARITHMIC,
	DATETIME,
	CATEGORY;


	/**
	 * Return a value that will be used by the Jackson library to serialize this enum into JSON.
	 *
	 * @return Lower case string for use in JSON objects
	 */
	@JsonValue
	public String toJson() {
		return this.toString().toLowerCase();
	}
}
