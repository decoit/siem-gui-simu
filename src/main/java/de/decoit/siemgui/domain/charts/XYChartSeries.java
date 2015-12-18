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
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * A chart series that allows definition of X- and Y-values of a data point.
 * X-values are of type Long, Y-value data type has to be defined.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <Y> Data type of the Y-values
 */
@Getter
@Setter
@ToString( callSuper = true )
@EqualsAndHashCode( callSuper = true )
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XYChartSeries<Y> extends AbstractChartSeries {
	@JsonProperty("data")
	private List<XYChartData<Y>> data;


	/**
	 * Create a new chart series.
	 *
	 * @param name Name for the series
	 */
	public XYChartSeries(String name) {
		this.name = name;
		this.data = new ArrayList<>();
	}
}
