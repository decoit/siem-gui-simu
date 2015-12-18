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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Data object for XYChartSeries that allows definition of X- and Y-values.
 * X-values are of type Long, Y-value data type has to be defined.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <Y> Data type of the Y-values
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class XYChartData<Y> {
	@JsonProperty("name")
	private String name;

	@JsonProperty("color")
	private String color;

	@JsonProperty("x")
	private Long x;

	@JsonProperty("y")
	private Y y;


	public XYChartData() {

	}


	/**
	 * Create a data object with the specified X- and Y-values.
	 *
	 * @param x X-value
	 * @param y Y-value
	 */
	public XYChartData(Long x, Y y) {
		this.x = x;
		this.y = y;
	}
}
