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

import de.decoit.siemgui.service.WebSocketHeartbeatService;
import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgHeartbeat;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSocketHeartbeatServiceTest {
	private final List<String> activeHeartbeatIds = new ArrayList<>();

	@Mock
	private SystemConfig sysConfMock;

	@Mock
	private SimpMessagingTemplate simpMessagingTemplateMock;

	@InjectMocks
	private WebSocketHeartbeatService service;


	@Before
	public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		when(sysConfMock.getHeartbeatThreshold()).thenReturn(2);

		Method init = service.getClass().getDeclaredMethod("init");
		init.setAccessible(true);
		init.invoke(service);

		Method generateHeartbeatId = service.getClass().getDeclaredMethod("generateHeartbeatId");
		generateHeartbeatId.setAccessible(true);

		for(int i=0; i<3; i++) {
			String hbid = (String) generateHeartbeatId.invoke(service);
			activeHeartbeatIds.add(hbid);
		}
	}


	@After
	public void tearDown() {
		activeHeartbeatIds.clear();

		reset(sysConfMock);
		reset(simpMessagingTemplateMock);
	}


	/**
	 * Test of validateClientAlive method, of class HeartbeatServiceImpl.
	 */
	@Test
	public void testValidateClientAlive() {
		System.out.println("HeartbeatServiceImpl.validateClientAlive()");

		for(int i=0; i<activeHeartbeatIds.size(); i++) {
			boolean expResult;

			if(i == 0) {
				expResult = false;
			}
			else {
				expResult = true;
			}

			boolean result = service.validateClientAlive(activeHeartbeatIds.get(i));
			assertEquals("Client alive result mismatch", expResult, result);
		}
	}


	/**
	 * Test of validateHeartbeatId method, of class HeartbeatServiceImpl.
	 */
	@Test
	public void testValidateHeartbeatId() {
		System.out.println("HeartbeatServiceImpl.validateHeartbeatId()");

		for(int i=0; i<activeHeartbeatIds.size(); i++) {
			boolean expResult;

			if(i < activeHeartbeatIds.size()-1) {
				expResult = false;
			}
			else {
				expResult = true;
			}

			boolean result = service.validateHeartbeatId(activeHeartbeatIds.get(i));
			assertEquals("Heartbeat valid result mismatch", expResult, result);
		}
	}


	/**
	 * Test of getCurrentHeartbeat method, of class HeartbeatServiceImpl.
	 */
	@Test
	public void testGetCurrentHeartbeat() {
		System.out.println("HeartbeatServiceImpl.getCurrentHeartbeat()");

		int i = activeHeartbeatIds.size() - 1;
		String expResult = activeHeartbeatIds.get(i);
		String result = service.getCurrentHeartbeat();

		assertEquals("Current heartbeat ID mismatch", expResult, result);
	}


	/**
	 * Test of sendHeartbeat method, of class HeartbeatServiceImpl.
	 */
	@Test
	public void testSendHeartbeat() {
		System.out.println("HeartbeatServiceImpl.sendHeartbeat()");

		service.sendHeartbeat();

		verify(simpMessagingTemplateMock).convertAndSend(eq("/topic/heartbeat"), any(MsgHeartbeat.class));
	}
}
