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
package de.decoit.siemgui.service.converter;

import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.simu.incidents.entities.IncidentEntity;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.After;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class GpmEngineCorrelationConverterTest {

	@Mock
	private SystemConfig sysConfMock;

	@Mock
	private DateConverter dateConvMock;

	@InjectMocks
	private GpmEngineCorrelationConverter converter;


	@Before
	public void setUp() {
		when(sysConfMock.getThreatLevelLowMin()).thenReturn(0);
		when(sysConfMock.getThreatLevelLowMax()).thenReturn(3);
		when(sysConfMock.getThreatLevelMedMin()).thenReturn(4);
		when(sysConfMock.getThreatLevelMedMax()).thenReturn(6);
		when(sysConfMock.getThreatLevelHighMin()).thenReturn(7);
		when(sysConfMock.getThreatLevelHighMax()).thenReturn(10);

		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 7, 12, 30, 15))).thenReturn(LocalDateTime.of(2014, 8, 7, 12, 30, 15));
	}


	@After
	public void tearDown() {
		reset(sysConfMock);
		reset(dateConvMock);
	}


	/**
	 * Test of convertIncident method, of class GpmEngineCorrelationConverter.
	 *
	 * @throws java.lang.Exception if converting IP string into InetAddres fails
	 */
	@Test
	public void testConvertIncident() throws Exception {
		System.out.println("GpmEngineCorrelationConverter.convertIncident()");

		IncidentEntity inc = new IncidentEntity();
		inc.setId(1);
		inc.setName("Test Incident");
		inc.setStatus(de.decoit.simu.incidents.enums.IncidentStatus.New);
		inc.setTimestamp(new Date(2014-1900, 7, 7, 12, 30, 15));
//		inc.setSourceIp("1.1.1.2");
//		inc.setDestinationIp("1.1.1.1");
		inc.setRisk(5);

		Incident expResult = new Incident();
		expResult.setId(1);
		expResult.setName("Test Incident");
		expResult.setStatus(IncidentStatus.NEW);
		expResult.setTimestamp(LocalDateTime.of(2014, 8, 7, 12, 30, 15));
//		expResult.setSource(InetAddress.getByName("1.1.1.2"));
//		expResult.setDestination(InetAddress.getByName("1.1.1.1"));
		expResult.setRisk(5);
		expResult.setThreatLevel(ThreatLevel.MEDIUM);

		Incident result = converter.convertIncident(inc);

		assertEquals("Incident ID mismatch", expResult.getId(), result.getId());
		assertEquals("Incident name mismatch", expResult.getName(), result.getName());
		assertEquals("Incident status mismatch", expResult.getStatus(), result.getStatus());
		assertEquals("Incident timestamp mismatch", expResult.getTimestamp(), result.getTimestamp());
//		assertEquals("Incident source mismatch", expResult.getSource(), result.getSource());
//		assertEquals("Incident destination mismatch", expResult.getDestination(), result.getDestination());
		assertEquals("Incident risk mismatch", expResult.getRisk(), result.getRisk());
		assertEquals("Incident threat level mismatch", expResult.getThreatLevel(), result.getThreatLevel());
	}


	/**
	 * Test of convertDomIncidentStatus method, of class GpmEngineCorrelationConverter.
	 */
	@Test
	public void testConvertDomIncidentStatus() {
		System.out.println("GpmEngineCorrelationConverter.convertDomIncidentStatus()");

		IncidentStatus status1 = IncidentStatus.NEW;
		IncidentStatus status2 = IncidentStatus.IN_PROGRESS;
		IncidentStatus status3 = IncidentStatus.DONE;
		IncidentStatus status4 = IncidentStatus.UNKNOWN;

		de.decoit.simu.incidents.enums.IncidentStatus expResult1 = de.decoit.simu.incidents.enums.IncidentStatus.New;
		de.decoit.simu.incidents.enums.IncidentStatus expResult2 = de.decoit.simu.incidents.enums.IncidentStatus.InProgress;
		de.decoit.simu.incidents.enums.IncidentStatus expResult3 = de.decoit.simu.incidents.enums.IncidentStatus.Done;
		de.decoit.simu.incidents.enums.IncidentStatus expResult4 = de.decoit.simu.incidents.enums.IncidentStatus.Unknown;

		de.decoit.simu.incidents.enums.IncidentStatus result1 = converter.convertDomIncidentStatus(status1);
		de.decoit.simu.incidents.enums.IncidentStatus result2 = converter.convertDomIncidentStatus(status2);
		de.decoit.simu.incidents.enums.IncidentStatus result3 = converter.convertDomIncidentStatus(status3);
		de.decoit.simu.incidents.enums.IncidentStatus result4 = converter.convertDomIncidentStatus(status4);

		assertEquals("Status NEW mismatch", expResult1, result1);
		assertEquals("Status IN_PROGRESS mismatch", expResult2, result2);
		assertEquals("Status DONE mismatch", expResult3, result3);
		assertEquals("Status UNKNOWN mismatch", expResult4, result4);
	}

}
