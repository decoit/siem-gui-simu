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

import de.decoit.siemgui.service.converter.RtTicketManagementConverter;
import de.decoit.siemgui.service.converter.DateConverter;
import de.decoit.siemgui.domain.NewTicket;
import de.decoit.siemgui.domain.Ticket;
import de.decoit.siemgui.domain.tickethistory.TicketHistoryItem;
import de.decoit.siemgui.domain.TicketQueue;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
import de.decoit.siemgui.service.TicketService;
import de.decoit.siemgui.service.UserDetailsService;
import de.decoit.rt.model.RtQueue;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicket.RtTicketStatus;
import de.decoit.rt.model.RtTicketHistoryItem;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
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


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class RtTicketManagementConverterTest {
	@Mock
	private UserDetailsService userDetailsServiceMock;

	@Mock
	private TicketService ticketServMock;

	@Mock
	private DateConverter dateConvMock;

	@InjectMocks
	private RtTicketManagementConverter converter;


	@Before
	public void setUp() throws ExternalServiceException {
		when(dateConvMock.localDateTimeToDate(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30))).thenReturn(new Date(2014-1900, 7, 15, 8, 15, 30));
		when(dateConvMock.localDateTimeToDate(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 59))).thenReturn(new Date(2014-1900, 7, 15, 12, 30, 59));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 8, 15, 30))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 54))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 54));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 55))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 55));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 56))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 56));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 57))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 57));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 58))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 58));
		when(dateConvMock.dateToLocalDateTime(new Date(2014-1900, 7, 15, 12, 30, 59))).thenReturn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 59));

		TicketQueue queue = new TicketQueue();
		queue.setId(1);
		queue.setName("SIEM");
		queue.setDescription("Testing queue for jUnit");

		when(ticketServMock.getTicketQueueDetails(eq("SIEM"), any(RtAuthorizedUser.class))).thenReturn(queue);

		User testuser = new User(1, "testuser", "Test User", true, true, true, false);

		when(userDetailsServiceMock.getUserDetails(eq("testuser"), any(RtAuthorizedUser.class))).thenReturn(testuser);
	}


	@After
	public void tearDown() {
		reset(userDetailsServiceMock);
		reset(ticketServMock);
		reset(dateConvMock);
	}


	/**
	 * Test of convertDomTicket method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertDomTicket() {
		System.out.println("RtTicketManagementConverter.convertDomTicket()");

		User owner = new User(1, "testuser", "Test User", true, true, true, false);

		TicketQueue queue = new TicketQueue();
		queue.setId(21);
		queue.setName("SIEM Test");
		queue.setDescription("Testing queue for jUnit");

		List<String> requestors = new ArrayList<>();
		requestors.add("req1@junit.local");
		requestors.add("req2@junit.local");
		requestors.add("req3@junit.local");

		Ticket ticket = new Ticket();
		ticket.setId(15);
		ticket.setTitle("Test ticket for jUnit");
		ticket.setCreator("test@junit.local");
		ticket.setCreatedOn(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30));
		ticket.setIncidentId(42);
		ticket.setOwner(owner);
		ticket.setPriority(10);
		ticket.setQueue(queue);
		ticket.setRequestors(requestors);
		ticket.setResolvedOn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 59));
		ticket.setRisk(5);
		ticket.setStatus(TicketStatus.NEW);
		ticket.setTimeWorked(60);

		RtTicket result = converter.convertDomTicket(ticket);

		assertEquals("Ticket ID mismatch", 15L, result.getId());
		assertEquals("Ticket title mismatch", "Test ticket for jUnit", result.getSubject());
		assertEquals("Ticket creator mismatch", "test@junit.local", result.getCreator());
		assertEquals("Ticket created on mismatch", new Date(2014-1900, 7, 15, 8, 15, 30), result.getCreated());
		assertEquals("Ticket incident ID mismatch", "42", result.getCustomFields().get("Incident"));
		assertEquals("Ticket owner mismatch", owner.getUsername(), result.getOwner());
		assertEquals("Ticket priority mismatch", 10, result.getPriority());
		assertEquals("Ticket queue mismatch", queue.getName(), result.getQueue());
		assertEquals("Ticket requestors mismatch", requestors, result.getRequestors());
		assertEquals("Ticket resolved on mismatch", new Date(2014-1900, 7, 15, 12, 30, 59), result.getResolved());
		assertEquals("Ticket risk mismatch", "5", result.getCustomFields().get("Risk"));
		assertEquals("Ticket status mismatch", RtTicketStatus.NEW, result.getStatus());
		assertEquals("Ticket time worked mismatch", 60, result.getTimeWorked());
	}


	/**
	 * Test of convertDomTicketList method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertDomTicketList() {
		System.out.println("RtTicketManagementConverter.convertDomTicketList()");

		User owner = new User(1, "testuser", "Test User", true, true, true, false);

		TicketQueue queue = new TicketQueue();
		queue.setId(21);
		queue.setName("SIEM Test");
		queue.setDescription("Testing queue for jUnit");

		List<String> requestors = new ArrayList<>();
		requestors.add("req1@junit.local");
		requestors.add("req2@junit.local");
		requestors.add("req3@junit.local");

		List<Ticket> ticketList = new ArrayList<>();
		for(int i=1; i<=5; i++) {
			Ticket ticket = new Ticket();
			ticket.setId(i);
			ticket.setTitle("Test ticket for jUnit");
			ticket.setCreator("test@junit.local");
			ticket.setCreatedOn(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30));
			ticket.setIncidentId(42);
			ticket.setOwner(owner);
			ticket.setPriority(10);
			ticket.setQueue(queue);
			ticket.setRequestors(requestors);
			ticket.setResolvedOn(LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 59));
			ticket.setRisk(5);
			ticket.setStatus(TicketStatus.NEW);
			ticket.setTimeWorked(60);

			ticketList.add(ticket);
		}

		List<RtTicket> result = converter.convertDomTicketList(ticketList);

		assertEquals("Ticket list size mismatch", 5, result.size());

		for(int i=1; i<=5; i++) {
			RtTicket ticket = result.get(i-1);

			assertEquals("Ticket ID mismatch", (long) i, ticket.getId());
			assertEquals("Ticket title mismatch", "Test ticket for jUnit", ticket.getSubject());
			assertEquals("Ticket creator mismatch", "test@junit.local", ticket.getCreator());
			assertEquals("Ticket created on mismatch", new Date(2014-1900, 7, 15, 8, 15, 30), ticket.getCreated());
			assertEquals("Ticket incident ID mismatch", "42", ticket.getCustomFields().get("Incident"));
			assertEquals("Ticket owner mismatch", owner.getUsername(), ticket.getOwner());
			assertEquals("Ticket priority mismatch", 10, ticket.getPriority());
			assertEquals("Ticket queue mismatch", queue.getName(), ticket.getQueue());
			assertEquals("Ticket requestors mismatch", requestors, ticket.getRequestors());
			assertEquals("Ticket resolved on mismatch", new Date(2014-1900, 7, 15, 12, 30, 59), ticket.getResolved());
			assertEquals("Ticket risk mismatch", "5", ticket.getCustomFields().get("Risk"));
			assertEquals("Ticket status mismatch", RtTicketStatus.NEW, ticket.getStatus());
			assertEquals("Ticket time worked mismatch", 60, ticket.getTimeWorked());
		}
	}


	/**
	 * Test of convertDomNewTicket method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertDomNewTicket() {
		System.out.println("RtTicketManagementConverter.convertDomNewTicket()");

		TicketQueue queue = new TicketQueue();
		queue.setId(21);
		queue.setName("SIEM Test");
		queue.setDescription("Testing queue for jUnit");

		NewTicket ticket = new NewTicket();
		ticket.setTitle("New test ticket");
		ticket.setRequestor("req1@junit.local");
		ticket.setQueue(queue);
		ticket.setRisk(5);
		ticket.setStarts(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30));
		ticket.setText("New test ticket\nfor use in jUnit");
		ticket.setIncidentId(42);

		RtTicket result = converter.convertDomNewTicket(ticket);

		List<String> expRequestors = new ArrayList<>();
		expRequestors.add("req1@junit.local");

		assertEquals("Ticket title mismatch", "New test ticket", result.getSubject());
		assertEquals("Ticket incident ID mismatch", "42", result.getCustomFields().get("Incident"));
		assertEquals("Ticket queue mismatch", queue.getName(), result.getQueue());
		assertEquals("Ticket requestors mismatch", expRequestors, result.getRequestors());
		assertEquals("Ticket risk mismatch", "5", result.getCustomFields().get("Risk"));
		assertEquals("Ticket starts mismatch", new Date(2014-1900, 7, 15, 8, 15, 30), result.getStarts());
		assertEquals("Ticket text mismatch", "New test ticket\nfor use in jUnit", result.getText());
	}


	/**
	 * Test of convertDomNewTicketList method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertDomNewTicketList() {
		System.out.println("RtTicketManagementConverter.convertDomNewTicketList()");

		TicketQueue queue = new TicketQueue();
		queue.setId(21);
		queue.setName("SIEM Test");
		queue.setDescription("Testing queue for jUnit");

		List<NewTicket> ticketList = new ArrayList<>();

		for(int i=0; i<5; i++) {
			NewTicket ticket = new NewTicket();
			ticket.setTitle("New test ticket " + i);
			ticket.setRequestor("req1@junit.local");
			ticket.setQueue(queue);
			ticket.setRisk(5);
			ticket.setStarts(LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30));
			ticket.setText("New test ticket\nfor use in jUnit");
			ticket.setIncidentId(42);

			ticketList.add(ticket);
		}

		List<RtTicket> result = converter.convertDomNewTicketList(ticketList);
		List<String> expRequestors = new ArrayList<>();
		expRequestors.add("req1@junit.local");

		for(int i=0; i<5; i++) {
			RtTicket ticket = result.get(i);

			assertEquals("Ticket title mismatch", "New test ticket " + i, ticket.getSubject());
			assertEquals("Ticket incident ID mismatch", "42", ticket.getCustomFields().get("Incident"));
			assertEquals("Ticket queue mismatch", queue.getName(), ticket.getQueue());
			assertEquals("Ticket requestors mismatch", expRequestors, ticket.getRequestors());
			assertEquals("Ticket risk mismatch", "5", ticket.getCustomFields().get("Risk"));
			assertEquals("Ticket starts mismatch", new Date(2014-1900, 7, 15, 8, 15, 30), ticket.getStarts());
			assertEquals("Ticket text mismatch", "New test ticket\nfor use in jUnit", ticket.getText());
		}
	}


	/**
	 * Test of convertQueue method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertQueue() {
		System.out.println("RtTicketManagementConverter.convertQueue()");

		Map<String, String> cf = new HashMap<>();

		RtQueue queue = new RtQueue();
		queue.setId(15);
		queue.setCommentAddress("comment@queue.local");
		queue.setCorrespondAddress("correspond@queue.local");
		queue.setCustomFields(cf);
		queue.setDefaultDueIn(30);
		queue.setDescription("Queue description");
		queue.setDisabled(false);
		queue.setFinalPriority(10);
		queue.setInitialPriority(0);
		queue.setName("Test queue");

		TicketQueue result = converter.convertQueue(queue);

		assertEquals("Queue ID mismatch", 15L, result.getId());
		assertEquals("Queue name mismatch", "Test queue", result.getName());
		assertEquals("Queue description mismatch", "Queue description", result.getDescription());
	}


	/**
	 * Test of convertQueueList method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertQueueList() {
		System.out.println("RtTicketManagementConverter.convertQueueList()");

		Map<String, String> cf = new HashMap<>();

		List<RtQueue> queueList = new ArrayList<>();

		for(int i=1; i<=5; i++) {
			RtQueue queue = new RtQueue();
			queue.setId(i);
			queue.setCommentAddress("comment@queue.local");
			queue.setCorrespondAddress("correspond@queue.local");
			queue.setCustomFields(cf);
			queue.setDefaultDueIn(30);
			queue.setDescription("Queue description");
			queue.setDisabled(false);
			queue.setFinalPriority(10);
			queue.setInitialPriority(0);
			queue.setName("Test queue");

			queueList.add(queue);
		}

		List<TicketQueue> result = converter.convertQueueList(queueList);

		for(int i=1; i<=5; i++) {
			TicketQueue queue = result.get(i-1);

			assertEquals("Queue ID mismatch", (long) i, queue.getId());
			assertEquals("Queue name mismatch", "Test queue", queue.getName());
			assertEquals("Queue description mismatch", "Queue description", queue.getDescription());
		}
	}


	/**
	 * Test of convertTicket method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertTicket() throws Exception {
		System.out.println("RtTicketManagementConverter.convertTicket()");

		List<String> adminCc = new ArrayList<>();
		adminCc.add("acc1@ticket.local");
		adminCc.add("acc2@ticket.local");

		List<String> cc = new ArrayList<>();
		cc.add("cc1@ticket.local");
		cc.add("cc2@ticket.local");

		List<String> requestors = new ArrayList<>();
		requestors.add("req1@junit.local");
		requestors.add("req2@junit.local");
		requestors.add("req3@junit.local");

		Map<String, String> cf = new HashMap<>();
		cf.put("Incident", "1");
		cf.put("Risk", "5");

		RtTicket ticket = new RtTicket();
		ticket.setAdminCc(adminCc);
		ticket.setCc(cc);
		ticket.setCreated(new Date(2014-1900, 7, 15, 8, 15, 30));
		ticket.setCreator("root");
		ticket.setCustomFields(cf);
		ticket.setDue(new Date(2014-1900, 7, 15, 12, 30, 54));
		ticket.setFinalPriority(10);
		ticket.setId(15);
		ticket.setInitialPriority(0);
		ticket.setLastUpdated(new Date(2014-1900, 7, 15, 12, 30, 55));
		ticket.setOwner("testuser");
		ticket.setPriority(5);
		ticket.setQueue("SIEM");
		ticket.setRequestors(requestors);
		ticket.setResolved(new Date(2014-1900, 7, 15, 12, 30, 56));
		ticket.setStarted(new Date(2014-1900, 7, 15, 12, 30, 57));
		ticket.setStarts(new Date(2014-1900, 7, 15, 12, 30, 58));
		ticket.setStatus(RtTicketStatus.NEW);
		ticket.setSubject("Test ticket for jUnit");
		ticket.setText("A ticket for unit testing with jUnit");
		ticket.setTimeEstimated(60);
		ticket.setTimeLeft(30);
		ticket.setTimeWorked(30);
		ticket.setTold(new Date(2014-1900, 7, 15, 12, 30, 59));

		AuthorizedUser user = new RtAuthorizedUser();

		Ticket result = converter.convertTicket(ticket, user);

		assertEquals("Ticket ID mismatch", 15L, result.getId());
		assertEquals("Ticket title mismatch", "Test ticket for jUnit", result.getTitle());
		assertEquals("Ticket creator mismatch", "root", result.getCreator());
		assertEquals("Ticket created on mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30), result.getCreatedOn());
		assertEquals("Ticket incident ID mismatch", 1L, result.getIncidentId());
		assertEquals("Ticket owner mismatch", "testuser", result.getOwner().getUsername());
		assertEquals("Ticket priority mismatch", 5, result.getPriority());
		assertEquals("Ticket queue mismatch", "SIEM", result.getQueue().getName());
		assertEquals("Ticket requestors mismatch", requestors, result.getRequestors());
		assertEquals("Ticket resolved on mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 56), result.getResolvedOn());
		assertEquals("Ticket risk mismatch", 5, result.getRisk());
		assertEquals("Ticket status mismatch", TicketStatus.NEW, result.getStatus());
		assertEquals("Ticket time worked mismatch", 30, result.getTimeWorked());
	}


	/**
	 * Test of convertTicketList method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertTicketList() throws Exception {
		System.out.println("RtTicketManagementConverter.convertTicketList()");

		List<String> adminCc = new ArrayList<>();
		adminCc.add("acc1@ticket.local");
		adminCc.add("acc2@ticket.local");

		List<String> cc = new ArrayList<>();
		cc.add("cc1@ticket.local");
		cc.add("cc2@ticket.local");

		List<String> requestors = new ArrayList<>();
		requestors.add("req1@junit.local");
		requestors.add("req2@junit.local");
		requestors.add("req3@junit.local");

		Map<String, String> cf = new HashMap<>();
		cf.put("Incident", "1");
		cf.put("Risk", "5");

		List<RtTicket> ticketList = new ArrayList<>();
		for(int i=1; i<=5; i++) {
			RtTicket ticket = new RtTicket();
			ticket.setAdminCc(adminCc);
			ticket.setCc(cc);
			ticket.setCreated(new Date(2014-1900, 7, 15, 8, 15, 30));
			ticket.setCreator("root");
			ticket.setCustomFields(cf);
			ticket.setDue(new Date(2014-1900, 7, 15, 12, 30, 54));
			ticket.setFinalPriority(10);
			ticket.setId(i);
			ticket.setInitialPriority(0);
			ticket.setLastUpdated(new Date(2014-1900, 7, 15, 12, 30, 55));
			ticket.setOwner("testuser");
			ticket.setPriority(5);
			ticket.setQueue("SIEM");
			ticket.setRequestors(requestors);
			ticket.setResolved(new Date(2014-1900, 7, 15, 12, 30, 56));
			ticket.setStarted(new Date(2014-1900, 7, 15, 12, 30, 57));
			ticket.setStarts(new Date(2014-1900, 7, 15, 12, 30, 58));
			ticket.setStatus(RtTicketStatus.NEW);
			ticket.setSubject("Test ticket for jUnit");
			ticket.setText("A ticket for unit testing with jUnit");
			ticket.setTimeEstimated(60);
			ticket.setTimeLeft(30);
			ticket.setTimeWorked(30);
			ticket.setTold(new Date(2014-1900, 7, 15, 12, 30, 59));

			ticketList.add(ticket);
		}

		AuthorizedUser user = new RtAuthorizedUser();
		List<Ticket> result = converter.convertTicketList(ticketList, user);

		for(int i=1; i<=5; i++) {
			Ticket ticket = result.get(i-1);

			assertEquals("Ticket ID mismatch", (long) i, ticket.getId());
			assertEquals("Ticket title mismatch", "Test ticket for jUnit", ticket.getTitle());
			assertEquals("Ticket creator mismatch", "root", ticket.getCreator());
			assertEquals("Ticket created on mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30), ticket.getCreatedOn());
			assertEquals("Ticket incident ID mismatch", 1L, ticket.getIncidentId());
			assertEquals("Ticket owner mismatch", "testuser", ticket.getOwner().getUsername());
			assertEquals("Ticket priority mismatch", 5, ticket.getPriority());
			assertEquals("Ticket queue mismatch", "SIEM", ticket.getQueue().getName());
			assertEquals("Ticket requestors mismatch", requestors, ticket.getRequestors());
			assertEquals("Ticket resolved on mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 12, 30, 56), ticket.getResolvedOn());
			assertEquals("Ticket risk mismatch", 5, ticket.getRisk());
			assertEquals("Ticket status mismatch", TicketStatus.NEW, ticket.getStatus());
			assertEquals("Ticket time worked mismatch", 30, ticket.getTimeWorked());
		}
	}


	/**
	 * Test of convertTicketHistory method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertTicketHistory() throws Exception {
		System.out.println("RtTicketManagementConverter.convertTicketHistory()");

		RtTicketHistoryItem historyItem = new RtTicketHistoryItem();
		historyItem.setId(15);
		historyItem.setTicketId(42);
		historyItem.setCreator("testuser");
		historyItem.setContent("History item content");
		historyItem.setDescription("History item description");
		historyItem.setCreated(new Date(2014-1900, 7, 15, 8, 15, 30));
		historyItem.setNewValue("5");
		historyItem.setOldValue("4");
		historyItem.setTimeTaken(30);
		historyItem.setType(RtTicketHistoryItem.RtTicketHistoryItemType.SET);
		historyItem.setField("A sample field");

		AuthorizedUser user = new RtAuthorizedUser();
		TicketHistoryItem result = converter.convertTicketHistory(historyItem, user);

		assertEquals("History item ID mismatch", 15L, result.getId());
		assertEquals("History item ticket ID mismatch", 42L, result.getTicketId());
		assertEquals("History item creator mismatch", "testuser", result.getCreator().getUsername());
//		assertEquals("History item content mismatch", "History item content", result.getText());
//		assertEquals("History item description mismatch", "History item description", result.getDescription());
//		assertEquals("History item created mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30), result.getCreated());
//		assertEquals("History item new value mismatch", "5", result.getNewValue());
//		assertEquals("History item old value mismatch", "4", result.getOldValue());
		assertEquals("History item time taken mismatch", 30, result.getTimeTaken());
//		assertEquals("History item type mismatch", TicketHistoryItem.TicketHistoryItemType.SET, result.getType());
//		assertEquals("History item field mismatch", "A sample field", result.getField());
	}


	/**
	 * Test of convertTicketHistoryList method, of class RtTicketManagementConverter.
	 */
	@Test
	public void testConvertTicketHistoryList() throws Exception {
		System.out.println("RtTicketManagementConverter.convertTicketHistoryList()");

		List<RtTicketHistoryItem> historyItemList = new ArrayList<>();
		for(int i=1; i<=5; i++) {
			RtTicketHistoryItem historyItem = new RtTicketHistoryItem();
			historyItem.setId(i);
			historyItem.setTicketId(42);
			historyItem.setCreator("testuser");
			historyItem.setContent("History item content");
			historyItem.setDescription("History item description");
			historyItem.setCreated(new Date(2014-1900, 7, 15, 8, 15, 30));
			historyItem.setNewValue("5");
			historyItem.setOldValue("4");
			historyItem.setTimeTaken(30);
			historyItem.setType(RtTicketHistoryItem.RtTicketHistoryItemType.SET);
			historyItem.setField("A sample field");

			historyItemList.add(historyItem);
		}

		AuthorizedUser user = new RtAuthorizedUser();
		List<TicketHistoryItem> result = converter.convertTicketHistoryList(historyItemList, user);

		for(int i=1; i<=5; i++) {
			TicketHistoryItem thi = result.get(i-1);

			assertEquals("History item ID mismatch", (long) i, thi.getId());
			assertEquals("History item ticket ID mismatch", 42L, thi.getTicketId());
			assertEquals("History item creator mismatch", "testuser", thi.getCreator().getUsername());
//			assertEquals("History item content mismatch", "History item content", thi.getContent());
//			assertEquals("History item description mismatch", "History item description", thi.getDescription());
//			assertEquals("History item created mismatch", LocalDateTime.of(2014, Month.AUGUST, 15, 8, 15, 30), thi.getCreated());
//			assertEquals("History item new value mismatch", "5", thi.getNewValue());
//			assertEquals("History item old value mismatch", "4", thi.getOldValue());
			assertEquals("History item time taken mismatch", 30, thi.getTimeTaken());
//			assertEquals("History item type mismatch", TicketHistoryItem.TicketHistoryItemType.SET, thi.getType());
//			assertEquals("History item field mismatch", "A sample field", thi.getField());
		}
	}

}
