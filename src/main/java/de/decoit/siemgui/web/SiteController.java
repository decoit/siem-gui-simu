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
package de.decoit.siemgui.web;

import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.AuthorizedUser;
import de.decoit.siemgui.service.UserDetailsService;
import de.decoit.siemgui.stomp.msgs.outgoing.MsgUserDetails;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * This controller provides mappings for general HTTP requests.
 * It allows the user to retrieve the welcome page and user details about himself.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Controller
public class SiteController {
	private final Logger LOG = LoggerFactory.getLogger(SiteController.class.getName());

	@Autowired
	private UserDetailsService userDetailsService;


	/**
	 * Send the welcome page index.html to the client.
	 *
	 * @param model JSP model
	 * @return Forward to the welcome page
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getWelcomePage(Model model) {
		return "/index";
	}


	/**
	 * Send details about the currently active user to the client.
	 * This is mapped to the URI /get-active-user
	 *
	 * @param principal Authentication information provided by Spring Security
	 * @return Outgoing message to include as HTTP response body
	 */
	@RequestMapping(value = "/get-active-user", method = RequestMethod.GET, produces = "application/json")
	@Secured("ROLE_SIEM_USER")
	@ResponseBody
	public MsgUserDetails getActiveUser(Principal principal) {
		MsgUserDetails msg = new MsgUserDetails();

		try {
			AuthorizedUser actUser = (AuthorizedUser) ((Authentication) principal).getPrincipal();
			User user = userDetailsService.getUserDetails(actUser.getUsername(), actUser);

			// Build the user details message
			msg.setUsername(user.getUsername());
			msg.setRealname(user.getRealName());
			msg.setPrivileged(user.isPrivileged());

			List<String> gas = ((Authentication) principal).getAuthorities().stream().map(GrantedAuthority::toString).collect(Collectors.toList());
			msg.setRoles(gas.toArray(new String[gas.size()]));
		}
		catch (ExternalServiceException | RuntimeException ex) {
			LOG.error("Error while fetching user details", ex);
			msg.setError(true);
		}

		return msg;
	}
}
