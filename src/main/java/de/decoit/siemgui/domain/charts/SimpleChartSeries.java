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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * A basic chart series that contains a list of Y-values.
 * X value will be selected by Highcharts, it cannot be set using this series type.
 *
 * @author Thomas Rix (rix@decoit.de)
 * @param <Y> Data type of the Y-values
 */
@Getter
@Setter
@ToString( callSuper = true )
@EqualsAndHashCode( callSuper = true )
public class SimpleChartSeries<Y> extends AbstractChartSeries {
	@JsonProperty("data")
	private List<Y> data;


	/**
	 * Create a new basic chart series.
	 *
	 * @param name Name for the series
	 */
	public SimpleChartSeries(String name) {
		this.name = name;
		this.data = new ArrayList<>();
	}


	/**
	 * Add a new data point to this series.
	 *
	 * @param point Y-value of the date point
	 */
	public void addDataPoint(Y point) {
		this.data.add(point);
	}
}
