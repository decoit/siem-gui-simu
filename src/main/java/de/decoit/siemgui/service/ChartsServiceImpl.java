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
package de.decoit.siemgui.service;

import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.domain.charts.AxisType;
import de.decoit.siemgui.domain.charts.ChartType;
import de.decoit.siemgui.domain.charts.SimpleChartSeries;
import de.decoit.siemgui.domain.charts.XAxisDefinition;
import de.decoit.siemgui.domain.charts.YAxisDefinition;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgChart;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Implementation of ChartsService that uses the chart domain objects defined in the package de.decoit.siemgui.domain.charts.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class ChartsServiceImpl implements ChartsService {
	@Autowired
	private IncidentService incServ;


	@Override
	public MsgChart getActiveIncidentsByThreatLevel(int chartIndex) throws ExternalServiceException {
		List<Incident> incList = incServ.getActiveIncidents();
		Map<ThreatLevel, List<Incident>> incidentsByThreatLevel = incList.stream().collect(Collectors.groupingBy(Incident::getThreatLevel));
		List<String> categories = new ArrayList<>();

		List<SimpleChartSeries<Long>> series = new ArrayList<>();
		SimpleChartSeries<Long> s1 = new SimpleChartSeries<>("Vorfälle");

		long lowCount = 0;
		if(incidentsByThreatLevel.containsKey(ThreatLevel.LOW)) {
			lowCount = incidentsByThreatLevel.get(ThreatLevel.LOW).size();
		}

		long mediumCount = 0;
		if(incidentsByThreatLevel.containsKey(ThreatLevel.MEDIUM)) {
			mediumCount = incidentsByThreatLevel.get(ThreatLevel.MEDIUM).size();
		}

		long highCount = 0;
		if(incidentsByThreatLevel.containsKey(ThreatLevel.HIGH)) {
			highCount = incidentsByThreatLevel.get(ThreatLevel.HIGH).size();
		}

		categories.add(ThreatLevel.HIGH.toString());
		s1.addDataPoint(highCount);

		categories.add(ThreatLevel.MEDIUM.toString());
		s1.addDataPoint(mediumCount);

		categories.add(ThreatLevel.LOW.toString());
		s1.addDataPoint(lowCount);

		// Add the series s1 to the list of series
		series.add(s1);

		// Create axis definitions
		XAxisDefinition xAxis = new XAxisDefinition(AxisType.CATEGORY, "Bedrohungsstufe");
		xAxis.setCategories(categories);

		YAxisDefinition yAxis = new YAxisDefinition(AxisType.LINEAR, "Vorfälle");

		// Create the chart message
		MsgChart msg = new MsgChart(chartIndex, ChartType.BAR, xAxis, yAxis);
		msg.setTitle("Aktive Vorfälle nach Bedrohungsstufe");
		msg.setSeries(series);

		return msg;
	}
}
