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

import de.decoit.siemgui.service.TicketService;
import de.decoit.siemgui.service.PushNotificationService;
import de.decoit.siemgui.service.UserDetailsService;
import de.decoit.siemgui.service.ScheduledIncidentService;
import de.decoit.siemgui.config.RootConfig;
import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.incidents.IncidentDao;
import de.decoit.siemgui.dao.incidents.filter.GpmEngineCorrelationHqlIncidentFilter;
import de.decoit.siemgui.dao.incidents.filter.IncidentFields;
import de.decoit.siemgui.dao.incidents.filter.IncidentFilter;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class ScheduledIncidentServiceTest {

	@Mock
	ApplicationContext ctxMock;

	@Mock
	IncidentDao incidentDaoMock;

	@Mock
	PushNotificationService pushNoteServMock;

	@Mock
	TicketService ticketServMock;

	@Mock
	UserDetailsService userDetailsServMock;

	@Mock
	RootConfig.SystemConfig sysConfMock;

	@InjectMocks
	private ScheduledIncidentService service;


	@Before
	public void setUp() throws ExternalServiceException, NoSuchFieldException, IllegalAccessException {
		when(ctxMock.getBean(IncidentFilter.class)).thenReturn(new GpmEngineCorrelationHqlIncidentFilter());

		List<Incident> incList = new ArrayList<>();
		for(int i=1; i<=5; i++) {
			Incident inc = new Incident();
			inc.setId(i);

			incList.add(inc);
		}

//		when(incidentDaoMock.searchIncidents(any(IncidentFilter.class), eq(IncidentFields.CREATED_ON), eq(OrderByDirection.DESCENDING))).thenReturn(incList);
		when(incidentDaoMock.getNewIncidents(-1L)).thenReturn(incList);
		when(incidentDaoMock.updateIncident(any(Incident.class))).thenReturn(new Incident());

		when(ticketServMock.updateTicket(any(Ticket.class), any(AuthorizedUser.class))).thenReturn(new Ticket());

		Field incidentMap = ScheduledIncidentService.class.getDeclaredField("incidentMap");
		incidentMap.setAccessible(true);

		HashMap<Long, Incident> incMap = new HashMap<>();
		User nobody = new User(100L, "Nobody", "Nobody", true, true, true, false);
		User testuser = new User(1L, "testuser", "Test User", true, true, true, false);

		for(long i=1; i<=6; i++) {
			Ticket t = new Ticket();
			if(i % 2 == 0) {
				t.setOwner(testuser);
			}
			else {
				t.setOwner(nobody);
			}

			Incident inc = new Incident();
			inc.setId(i);
			inc.setTicket(t);

			if(i <= 2) {
				inc.setStatus(IncidentStatus.NEW);
				inc.setRisk(2);
				inc.setThreatLevel(ThreatLevel.LOW);
			}
			else if(i <= 5) {
				inc.setStatus(IncidentStatus.IN_PROGRESS);
				inc.setRisk(6);
				inc.setThreatLevel(ThreatLevel.MEDIUM);
			}
			else {
				inc.setStatus(IncidentStatus.UNKNOWN);
				inc.setRisk(8);
				inc.setThreatLevel(ThreatLevel.HIGH);
			}
			incMap.put(i, inc);
		}

		incidentMap.set(service, incMap);
	}


	@After
	public void tearDown() {
		reset(ctxMock);
		reset(incidentDaoMock);
		reset(pushNoteServMock);
		reset(ticketServMock);
		reset(userDetailsServMock);
		reset(sysConfMock);
	}


	/**
	 * Test of getIncident method, of class ScheduledIncidentService.
	 */
	@Test
	public void testGetIncident() throws Exception {
		System.out.println("ScheduledIncidentService.getIncident()");

		long id = 1L;
		Incident result = service.getIncident(id);

		assertEquals(1L, result.getId());
		assertEquals(2, result.getRisk());
		assertEquals(ThreatLevel.LOW, result.getThreatLevel());
	}


	/**
	 * Test of countIncidentsForOverview method, of class ScheduledIncidentService.
	 */
	@Test
	public void testCountIncidentsForOverview() throws Exception {
		System.out.println("ScheduledIncidentService.countIncidentsForOverview()");

		Map<String, Long> expResult = new HashMap<>();
		expResult.put("new", 2L);
		expResult.put("inProgress", 3L);
		expResult.put("unknown", 1L);

		expResult.put("lowRisk", 2L);
		expResult.put("mediumRisk", 3L);
		expResult.put("highRisk", 1L);

		Map<String, Long> result = service.countIncidentsForOverview();

		assertEquals(expResult, result);
	}


	/**
	 * Test of calculateThreatLevel method, of class ScheduledIncidentService.
	 */
	@Test
	public void testCalculateThreatLevel() {
		System.out.println("ScheduledIncidentService.calculateThreatLevel()");

		Map<ThreatLevel, Long> expResult = new HashMap<>();
		expResult.put(ThreatLevel.LOW, 2L);
		expResult.put(ThreatLevel.MEDIUM, 3L);
		expResult.put(ThreatLevel.HIGH, 1L);

		Map<ThreatLevel, Long> result = service.calculateThreatLevel();

		assertEquals(expResult, result);
	}


	/**
	 * Test of getActiveIncidents method, of class ScheduledIncidentService.
	 */
	@Test
	public void testGetActiveIncidents() {
		System.out.println("ScheduledIncidentService.getActiveIncidents()");

		List<Incident> result = service.getActiveIncidents();

		assertEquals(6, result.size());
	}


	/**
	 * Test of getResolvedIncidents method, of class ScheduledIncidentService.
	 */
	@Test
	public void testGetResolvedIncidents() {
		System.out.println("ScheduledIncidentService.getResolvedIncidents()");

		List<Incident> result = service.getResolvedIncidents();

		assertEquals(0, result.size());
	}


	/**
	 * Test of takeIncident method, of class ScheduledIncidentService.
	 */
	@Test
	public void testTakeIncident() throws Exception {
		System.out.println("ScheduledIncidentService.takeIncident()");

		long incidentId = 1L;
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		service.takeIncident(incidentId, user);

		verify(ticketServMock).updateTicket(any(Ticket.class), any(AuthorizedUser.class));
	}


	/**
	 * Test of beginWorkOnIncident method, of class ScheduledIncidentService.
	 */
	@Test
	public void testBeginWorkOnIncident() throws Exception {
		System.out.println("ScheduledIncidentService.beginWorkOnIncident()");

		long incidentId = 2L;
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		service.beginWorkOnIncident(incidentId, user);

		verify(ticketServMock).updateTicket(any(Ticket.class), any(AuthorizedUser.class));
		verify(incidentDaoMock).updateIncident(any(Incident.class));
	}


	/**
	 * Test of finishWorkOnIncident method, of class ScheduledIncidentService.
	 */
	@Test
	public void testFinishWorkOnIncident() throws Exception {
		System.out.println("ScheduledIncidentService.finishWorkOnIncident()");

		long incidentId = 4L;
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		service.finishWorkOnIncident(incidentId, user);

		verify(ticketServMock).updateTicket(any(Ticket.class), any(AuthorizedUser.class));
		verify(incidentDaoMock).updateIncident(any(Incident.class));
	}


	/**
	 * Test of incidentLookup method, of class ScheduledIncidentService.
	 */
	@Test
	public void testIncidentLookup() throws ExternalServiceException {
		System.out.println("ScheduledIncidentService.incidentLookup()");

		service.incidentLookup();

//		verify(ctxMock).getBean(IncidentFilter.class);
//		verify(incidentDaoMock).searchIncidents(any(IncidentFilter.class), eq(IncidentFields.CREATED_ON), eq(OrderByDirection.DESCENDING));
		verify(incidentDaoMock).getNewIncidents(-1L);
		verify(pushNoteServMock).sendPushNotification(any(String.class));
	}
}
