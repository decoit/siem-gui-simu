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

import de.decoit.siemgui.service.UserDetailsServiceImpl;
import de.decoit.siemgui.dao.users.UserDao;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.security.RtAuthorizedUser;
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
public class UserDetailsServiceImplTest {
	@Mock
	private UserDao userDaoMock;

	@InjectMocks
	private UserDetailsServiceImpl service;


	@Before
	public void setUp() throws ExternalServiceException {
		User u = new User(1, "testuser", "Test User", true, true, true, false);
		when(userDaoMock.getUserDetails(eq("testuser"), any(AuthorizedUser.class))).thenReturn(u);
	}


	@After
	public void tearDown() {
		reset(userDaoMock);
	}


	/**
	 * Test of getUserDetails method, of class UserDetailsServiceImpl.
	 */
	@Test
	public void testGetUserDetails() throws Exception {
		System.out.println("UserDetailsServiceImpl.getUserDetails()");

		String username = "testuser";
		AuthorizedUser user = new RtAuthorizedUser();

		User result = service.getUserDetails(username, user);

		assertEquals(1L, result.getId());
		assertEquals("testuser", result.getUsername());
		assertEquals("Test User", result.getRealName());
	}

}
