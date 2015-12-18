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
package de.decoit.siemgui.stomp.msgs.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.decoit.siemgui.domain.charts.AbstractChartSeries;
import de.decoit.siemgui.domain.charts.ChartType;
import de.decoit.siemgui.domain.charts.XAxisDefinition;
import de.decoit.siemgui.domain.charts.YAxisDefinition;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * An outgoing message that contains information to draw a specific chart.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MsgChart {
	@JsonProperty("chartIndex")
	private int chartIndex;

	@JsonProperty("type")
	private ChartType type;

	@JsonProperty("title")
	private String title;

	@JsonProperty("series")
	private List<? extends AbstractChartSeries> series;

	@JsonProperty("xAxis")
	private XAxisDefinition xAxis;

	@JsonProperty("yAxis")
	private YAxisDefinition yAxis;


	/**
	 * Every chart must have an index, a type and definitions for X and Y axis.
	 *
	 * @param chartIndex Chart index provided by the frontend
	 * @param type       Type of the chart
	 * @param xAxis      X axis definition
	 * @param yAxis      Y axis definition
	 */
	public MsgChart(int chartIndex, ChartType type, XAxisDefinition xAxis, YAxisDefinition yAxis) {
		this.chartIndex = chartIndex;
		this.type = type;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}
}
