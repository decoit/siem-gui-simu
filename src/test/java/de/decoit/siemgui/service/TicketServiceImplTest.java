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

import de.decoit.siemgui.service.TicketServiceImpl;
import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.tickets.TicketDao;
import de.decoit.siemgui.dao.tickets.TicketQueueDao;
import de.decoit.siemgui.dao.tickets.filter.RtTicketFilter;
import de.decoit.siemgui.dao.tickets.filter.TicketFields;
import de.decoit.siemgui.dao.tickets.filter.TicketFilter;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedSystemUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
import java.lang.reflect.Field;
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
import org.springframework.context.ApplicationContext;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

	@Mock
	ApplicationContext ctxMock;

	@Mock
	SystemConfig sysConfMock;

	@Mock
	TicketDao ticketDaoMock;

	@Mock
	TicketQueueDao ticketQueueDaoMock;

	@Mock
	RtAuthorizedSystemUser authSysUserMock;

	@InjectMocks
	private TicketServiceImpl service;


	@Before
	public void setUp() throws ExternalServiceException, NoSuchFieldException, IllegalAccessException {
		when(ctxMock.getBean(TicketFilter.class)).thenReturn(getTicketFilter());

		when(sysConfMock.getSiemTicketQueueName()).thenReturn("SIEM");
		when(sysConfMock.getIncidentIdCustomField()).thenReturn("Incident");
		when(sysConfMock.getIncidentRiskCustomField()).thenReturn("Risk");

		when(authSysUserMock.getUsername()).thenReturn("siemsystem");

		TicketQueue tq = new TicketQueue();
		tq.setId(5);
		tq.setName("SIEM");
		when(ticketQueueDaoMock.getTicketQueueDetails(eq("SIEM"), any(AuthorizedUser.class))).thenReturn(tq);

		when(ticketDaoMock.createTicket(any(NewTicket.class), any(AuthorizedUser.class))).thenReturn(101L);

		// New ticket created for incident
		Ticket ticket = new Ticket();
		ticket.setId(101);
		when(ticketDaoMock.getTicketDetails(eq(101L), any(AuthorizedUser.class))).thenReturn(ticket);

		// Returned list for getAllSiemTickets()
		TicketFilter allSiemTicketsFilter = getTicketFilter();
		allSiemTicketsFilter
			.queueIs("SIEM")
			.and()
			.leftParenthesis()
				.statusIs(TicketStatus.NEW)
				.or()
				.statusIs(TicketStatus.OPEN)
				.or()
				.statusIs(TicketStatus.RESOLVED)
			.rightParenthesis();

		List<Ticket> allTicketsList = new ArrayList<>();
		for(int i=1; i<=5; i++) {
			Ticket t = new Ticket();
			t.setId(i);

			allTicketsList.add(t);
		}
		when(ticketDaoMock.searchTickets(eq(allSiemTicketsFilter), eq(TicketFields.CREATED_ON), eq(OrderByDirection.ASCENDING), any(AuthorizedUser.class))).thenReturn(allTicketsList);

		// Returned list for getSiemTicketsWithStatus()
		TicketFilter siemTicketsWithStatusFilter = getTicketFilter();
		siemTicketsWithStatusFilter
			.queueIs("SIEM")
			.and()
			.statusIs(TicketStatus.OPEN);

		List<Ticket> statusTicketsList = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			Ticket t = new Ticket();
			t.setId(i);

			statusTicketsList.add(t);
		}
		when(ticketDaoMock.searchTickets(eq(siemTicketsWithStatusFilter), eq(TicketFields.CREATED_ON), eq(OrderByDirection.ASCENDING), any(AuthorizedUser.class))).thenReturn(statusTicketsList);

		// Returned list for getTicketForIncident() with 1 result
		TicketFilter ticketForIncident8Filter = getTicketFilter();
		ticketForIncident8Filter
			.queueIs("SIEM")
			.and()
			.incidentIs(8);

		List<Ticket> incTicketList = new ArrayList<>();
		Ticket t = new Ticket();
		t.setId(15);
		incTicketList.add(t);
		when(ticketDaoMock.searchTickets(eq(ticketForIncident8Filter), eq(TicketFields.SUBJECT), eq(OrderByDirection.ASCENDING), any(AuthorizedUser.class))).thenReturn(incTicketList);

		Ticket td = new Ticket();
		td.setId(111L);
		when(ticketDaoMock.getTicketDetails(eq(111L), any(AuthorizedUser.class))).thenReturn(td);

		when(ticketDaoMock.updateTicket(any(Ticket.class), any(AuthorizedUser.class))).thenReturn(Boolean.TRUE);
	}


	@After
	public void tearDown() {
		reset(ctxMock);
		reset(sysConfMock);
		reset(ticketDaoMock);
		reset(ticketQueueDaoMock);
		reset(authSysUserMock);
	}


	/**
	 * Mock the SystemConfig reference in RtTicketFilter to prevent NullPointerExceptions
	 *
	 * @return A mocked RtTicketFilter
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private TicketFilter getTicketFilter() throws NoSuchFieldException, IllegalAccessException {
		Field sysConf = RtTicketFilter.class.getDeclaredField("sysConf");
		sysConf.setAccessible(true);

		RtTicketFilter tf = new RtTicketFilter();
		sysConf.set(tf, sysConfMock);

		return tf;
	}


	/**
	 * Test of getAllSiemTickets method, of class TicketServiceImpl.
	 */
	@Test
	public void testGetAllSiemTickets() throws Exception {
		System.out.println("TicketServiceImpl.getAllSiemTickets()");

		TicketFields orderByField = TicketFields.CREATED_ON;
		OrderByDirection orderByDirection = OrderByDirection.ASCENDING;
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		List<Ticket> result = service.getAllSiemTickets(orderByField, orderByDirection, user);

		assertEquals(5, result.size());
	}


	/**
	 * Test of getSiemTicketsWithStatus method, of class TicketServiceImpl.
	 */
	@Test
	public void testGetSiemTicketsWithStatus() throws Exception {
		System.out.println("TicketServiceImpl.getSiemTicketsWithStatus()");

		TicketStatus status = TicketStatus.OPEN;
		TicketFields orderByField = TicketFields.CREATED_ON;
		OrderByDirection orderByDirection = OrderByDirection.ASCENDING;
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		List<Ticket> result = service.getSiemTicketsWithStatus(status, orderByField, orderByDirection, user);

		assertEquals(3, result.size());
	}


	/**
	 * Test of getTicketForIncident method, of class TicketServiceImpl.
	 */
	@Test
	public void testGetTicketForIncident() throws Exception {
		System.out.println("TicketServiceImpl.getTicketForIncident()");

		Incident inc1 = new Incident();
		inc1.setId(8);

		Ticket result1 = service.getTicketForIncident(inc1);
		assertEquals("Ticket ID mismatch", 15L, result1.getId());
	}


	/**
	 * Test of updateTicket method, of class TicketServiceImpl.
	 */
	@Test
	public void testUpdateTicket() throws Exception {
		System.out.println("TicketServiceImpl.updateTicket()");

		Ticket ticket = new Ticket();
		ticket.setId(111L);
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		Ticket result = service.updateTicket(ticket, user);

		assertEquals("Ticket ID mismatch", 111L, result.getId());
	}


	/**
	 * Test of getTicketQueueDetails method, of class TicketServiceImpl.
	 */
	@Test
	public void testGetTicketQueueDetails() throws Exception {
		System.out.println("TicketServiceImpl.getTicketQueueDetails()");

		String queueName = "SIEM";
		AuthorizedUser user = new RtAuthorizedUser();
		user.setUsername("testuser");

		TicketQueue result = service.getTicketQueueDetails(queueName, user);

		assertEquals("Queue ID mismatch", 5L, result.getId());
		assertEquals("Queue name mismatch", "SIEM", result.getName());
	}
}
