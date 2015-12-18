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
package de.decoit.siemgui.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.decoit.siemgui.dao.tickets.filter.RtTicketFilter;
import de.decoit.siemgui.dao.tickets.filter.TicketFilter;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.security.RtAuthorizedSystemUser;
import de.decoit.siemgui.service.UserAuthenticationService;
import de.decoit.rt.RtConnector;
import de.decoit.rt.rest.RtRestConnector;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Configuration file for access to external services. This includes the RT REST API, Hibernate access to MySQL correlation database
 * and the Icinga event log. Additionally it scans for @Repository and @Entity annotated classes in the following packages:<br>
 * - de.decoit.siemgui.dao (GUI DAO classes)
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({ "de.decoit.siemgui.dao" })
@PropertySource({ "classpath:siem-gui.properties" })
public class PersistenceConfig {
	@Resource
	private Environment env;

	@Autowired
	ApplicationContext ctx;


	/**
	 * Bean definition for the RtConnector, prototype scoped.
	 *
	 * @return RtConnector bean
	 */
	@Bean
	@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RtConnector getRtConnector() {
		return new RtRestConnector(env.getProperty("rt.baseuri"));
	}


	@Bean
	@Qualifier("icingamapper")
	public ObjectMapper getJacksonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		return mapper;
	}


	/**
	 * Bean definition for the TicketFilter, prototype scoped.
	 *
	 * @return TicketFilter bean
	 */
	@Bean
	@Scope(value = "prototype")
	public TicketFilter getTicketFilter() {
		return new RtTicketFilter();
	}


	/**
	 * Bean definition for the IncidentFilter, prototype scoped.
	 *
	 * @return IncidentFilter bean
	 */
//	@Bean
//	@Scope(value = "prototype")
//	public IncidentFilter getIncidentFilter() {
//		return new CorrelationHqlIncidentFilter();
//	}


	@Bean
	public RtAuthorizedSystemUser getRtAuthorizedSystemUser() throws ExternalServiceException {
		RtAuthorizedSystemUser asu = new RtAuthorizedSystemUser();

		asu.setUsername(env.getProperty("rt.integration.systemusername"));
		asu.setPassword(env.getProperty("rt.integration.systempassword"));
		asu.setUserAuthServ(ctx.getBean(UserAuthenticationService.class));
		asu.login();

		return asu;
	}
}
