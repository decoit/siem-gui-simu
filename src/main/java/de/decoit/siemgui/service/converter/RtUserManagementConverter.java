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

import de.decoit.siemgui.config.RootConfig;
import de.decoit.siemgui.domain.User;
import de.decoit.siemgui.exception.ConversionException;
import de.decoit.rt.model.RtUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * This implementation of UserManagementConverter can be used to convert RtUser objects into the User domain object.
 * RtUser objects are used by the DECOIT RT Connector.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class RtUserManagementConverter implements UserManagementConverter<RtUser> {
	private final Logger LOG = LoggerFactory.getLogger(RtUserManagementConverter.class.getName());

	@Autowired
	private RootConfig.SystemConfig sysConf;


	@Override
	public User convertUser(RtUser usr) throws ConversionException {
		if(usr == null) {
			throw new ConversionException("Received null pointer for RtUser conversion");
		}

		boolean isEnabled = !usr.isDisabled();
		boolean siemAuthorized = false;
		boolean siemAdminAuthorized = false;

		Map<String, String> custFields = usr.getCustomFields();

		if(LOG.isDebugEnabled()) {
			LOG.debug("User custom fields: " + custFields.toString());
		}

		if(custFields.containsKey(sysConf.getSiemAccessCustomField()) && custFields.get(sysConf.getSiemAccessCustomField()).equals("1")) {
			siemAuthorized = true;
		}
		if(custFields.containsKey(sysConf.getSiemAdminAccessCustomField()) && custFields.get(sysConf.getSiemAdminAccessCustomField()).equals("1")) {
			siemAdminAuthorized = true;
		}

		User user = new User(usr.getId(), usr.getName(), usr.getRealName(), isEnabled, usr.isPrivileged(), siemAuthorized, siemAdminAuthorized);

		return user;
	}


	@Override
	public List<User> convertUserList(List<RtUser> usrList) throws ConversionException {
		if(usrList == null) {
			throw new ConversionException("Received null pointer for List<RtUser> conversion");
		}

		List<User> domList = usrList.stream().map(u -> convertUser(u)).collect(Collectors.toList());

		return domList;
	}


	@Override
	public RtUser convertDomUser(User user) throws ConversionException {
		if(user == null) {
			throw new ConversionException("Received null pointer for User conversion");
		}

		boolean isDisabled = !user.isEnabled();
		boolean isPrivileged = user.isPrivileged();
		Map<String, String> custFields = new HashMap<>();

		if(user.isSiemAuthorized()) {
			isPrivileged = true;
			custFields.put(sysConf.getSiemAccessCustomField(), "1");
		}
		else {
			custFields.put(sysConf.getSiemAccessCustomField(), "0");
		}

		if(user.isSiemAuthorized() && user.isSiemAdminAuthorized()) {
			custFields.put(sysConf.getSiemAdminAccessCustomField(), "1");
		}
		else {
			custFields.put(sysConf.getSiemAdminAccessCustomField(), "0");
		}

		RtUser rtu = new RtUser();

		rtu.setId(user.getId());
		rtu.setRealName(user.getRealName());
		rtu.setDisabled(isDisabled);
		rtu.setPrivileged(isPrivileged);
		rtu.setCustomFields(custFields);

		return rtu;
	}
}
