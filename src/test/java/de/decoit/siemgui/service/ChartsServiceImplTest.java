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
import de.decoit.siemgui.domain.charts.SimpleChartSeries;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgChart;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class ChartsServiceImplTest {
	@Mock
	private IncidentService incServMock;

	@InjectMocks
	private ChartsServiceImpl service;

	LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);


	@Before
	public void setUp() throws ExternalServiceException {
		List<Incident> incList = new ArrayList<>();
		for(int i=0; i<15; i++) {
			Incident inc = new Incident();

			if(i < 5) {
				// 5 low risk
				inc.setThreatLevel(ThreatLevel.LOW);
			}
			else if(i < 12) {
				// 7 medium risk
				inc.setThreatLevel(ThreatLevel.MEDIUM);
			}
			else {
				// 3 high risk
				inc.setThreatLevel(ThreatLevel.HIGH);
			}

			incList.add(inc);
		}
		when(incServMock.getActiveIncidents()).thenReturn(incList);
	}


	@After
	public void tearDown() {
		reset(incServMock);
	}


	/**
	 * Test of getActiveIncidentsByThreatLevel method, of class ChartsServiceImpl.
	 */
	@Test
	public void testGetActiveIncidentsByThreatLevel() throws Exception {
		System.out.println("ChartsServiceImpl.getActiveIncidentsByThreatLevel()");

		int chartIndex = 0;
		MsgChart result = service.getActiveIncidentsByThreatLevel(chartIndex);

		assertEquals("Chart index mismatch", 0, result.getChartIndex());

		SimpleChartSeries<Long> scs = (SimpleChartSeries<Long>) result.getSeries().get(0);
		List<Long> dataList = scs.getData();

		assertEquals("Chart data list size mismatch", 3, dataList.size());

		List<String> cats = result.getXAxis().getCategories();

		int lowIndex = cats.indexOf("LOW");
		int medIndex = cats.indexOf("MEDIUM");
		int highIndex = cats.indexOf("HIGH");
		assertEquals(Long.valueOf(5), dataList.get(lowIndex));
		assertEquals(Long.valueOf(7), dataList.get(medIndex));
		assertEquals(Long.valueOf(3), dataList.get(highIndex));
	}
}
