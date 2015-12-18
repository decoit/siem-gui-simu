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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Definition class for the Y axis that is compatible with Highcharts.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YAxisDefinition {
	@JsonProperty("type")
	private AxisType type;

	@JsonProperty("title")
	private String title;


	/**
	 * Create a new Y axis definition.
	 * Axis must have a type and title.
	 *
	 * @param type  Type of the axis
	 * @param title Label for the axis
	 */
	public YAxisDefinition(AxisType type, String title) {
		this.type = type;
		this.title = title;
	}
}
