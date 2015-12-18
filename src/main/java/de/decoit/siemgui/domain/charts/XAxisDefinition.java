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
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Definition class for the X axis that is compatible with Highcharts.
 * Setting categories is only required if the axis type is CATEGORY.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@ToString
@EqualsAndHashCode( exclude = { "categories" } )
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XAxisDefinition {
	@Getter
	@JsonProperty("type")
	private final AxisType type;

	@Getter
	@Setter
	@JsonProperty("categories")
	private List<String> categories;

	@Getter
	@JsonProperty("title")
	private final String title;


	/**
	 * Create a new X axis definition.
	 * Axis must have a type and title.
	 *
	 * @param type  Type of the axis
	 * @param title Label for the axis
	 */
	public XAxisDefinition(AxisType type, String title) {
		this.type = type;
		this.title = title;
		this.categories = null;
	}
}
