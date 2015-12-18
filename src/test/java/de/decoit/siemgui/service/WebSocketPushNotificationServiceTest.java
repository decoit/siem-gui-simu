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

import de.decoit.siemgui.service.WebSocketPushNotificationService;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgPushNotification;
import org.junit.After;
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
public class WebSocketPushNotificationServiceTest {
	@Mock
	private SimpMessagingTemplate simpMessagingTemplateMock;

	@InjectMocks
	private WebSocketPushNotificationService service;


	@Before
	public void setUp() {
	}


	@After
	public void tearDown() {
		reset(simpMessagingTemplateMock);
	}


	/**
	 * Test of sendPushNotification method, of class WebSocketPushNotificationService.
	 */
	@Test
	public void testSendPushNotification() {
		System.out.println("WebSocketPushNotificationService.sendPushNotification()");

		String notificationText = "This is a notification for testing";

		service.sendPushNotification(notificationText);

		verify(simpMessagingTemplateMock).convertAndSend(eq("/topic/push"), any(MsgPushNotification.class));
	}

}
