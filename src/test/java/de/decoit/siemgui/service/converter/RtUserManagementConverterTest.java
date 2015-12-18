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

import de.decoit.siemgui.service.converter.RtUserManagementConverter;
import de.decoit.siemgui.config.RootConfig;
import de.decoit.siemgui.domain.User;
import de.decoit.rt.model.RtUser;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@RunWith(MockitoJUnitRunner.class)
public class RtUserManagementConverterTest {
	@Mock
	private RootConfig.SystemConfig sysConfMock;

	@InjectMocks
	private RtUserManagementConverter converter;

	
	@Before
	public void setUp() {
		when(sysConfMock.getSiemAccessCustomField()).thenReturn("SIEM");
	}


	@After
	public void tearDown() {
		reset(sysConfMock);
	}


	/**
	 * Test of convert method, of class RtUserManagementConverter.
	 */
	@Test
	public void testConvert() {
		System.out.println("RtUserManagementConverter.convert()");

		RtUser usr1 = new RtUser();
		usr1.setId(15);
		usr1.setName("testuser");
		usr1.setRealName("Test User");
		usr1.setDisabled(false);
		usr1.setPrivileged(true);
		usr1.addCustomField("SIEM", "1");

		User result1 = converter.convertUser(usr1);

		assertEquals("User ID mismatch", 15L, result1.getId());
		assertEquals("Username mismatch", "testuser", result1.getUsername());
		assertEquals("Real name mismatch", "Test User", result1.getRealName());
		assertEquals("User enabled mismatch", true, result1.isEnabled());
		assertEquals("User privileged mismatch", true, result1.isPrivileged());
		assertEquals("User SIEM access mismatch", true, result1.isSiemAuthorized());

		RtUser usr2 = new RtUser();
		usr2.setId(16);
		usr2.setName("testuser2");
		usr2.setRealName("Test User 2");
		usr2.setDisabled(true);
		usr2.setPrivileged(false);
		usr2.addCustomField("SIEM", "0");

		User result2 = converter.convertUser(usr2);

		assertEquals("User ID mismatch", 16L, result2.getId());
		assertEquals("Username mismatch", "testuser2", result2.getUsername());
		assertEquals("Real name mismatch", "Test User 2", result2.getRealName());
		assertEquals("User enabled mismatch", false, result2.isEnabled());
		assertEquals("User privileged mismatch", false, result2.isPrivileged());
		assertEquals("User SIEM access mismatch", false, result2.isSiemAuthorized());
	}
}
