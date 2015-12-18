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

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Configuration class for anything that is not configured in one of the specific configurations.
 * This includes the services in de.decoit.siemgui.service package and the general system configuration.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = {"de.decoit.siemgui.service", "de.decoit.siemgui.stomp.interceptors"})
@PropertySource({"classpath:siem-gui.properties"})
public class RootConfig {
	private final Logger LOG = LoggerFactory.getLogger(RootConfig.class.getName());

	@Resource
	private Environment env;


	/**
	 * Bean factory method for SystemConfig, an object that stores general configuration information.
	 *
	 * @return SystemConfig bean
	 */
	@Bean
	public SystemConfig getSystemConfig() {
		SystemConfig sysconf = new SystemConfig();

		sysconf.setSiemAccessCustomField(env.getProperty("rt.integration.cf-access"));
		sysconf.setSiemAdminAccessCustomField(env.getProperty("rt.integration.cf-admin"));
		sysconf.setSiemTicketQueueName(env.getProperty("rt.integration.siem-queue"));
		sysconf.setIncidentIdCustomField(env.getProperty("rt.integration.cf-incident"));
		sysconf.setIncidentRiskCustomField(env.getProperty("rt.integration.cf-risk"));
		sysconf.setThreatLevelLowMin(env.getProperty("threatlevel.low.min", Integer.class));
		sysconf.setThreatLevelLowMax(env.getProperty("threatlevel.low.max", Integer.class));
		sysconf.setThreatLevelLowDueIn(parsePeriodProperty(env.getProperty("threatlevel.low.duein")));
		sysconf.setThreatLevelMedMin(env.getProperty("threatlevel.med.min", Integer.class));
		sysconf.setThreatLevelMedMax(env.getProperty("threatlevel.med.max", Integer.class));
		sysconf.setThreatLevelMedDueIn(parsePeriodProperty(env.getProperty("threatlevel.med.duein")));
		sysconf.setThreatLevelHighMin(env.getProperty("threatlevel.high.min", Integer.class));
		sysconf.setThreatLevelHighMax(env.getProperty("threatlevel.high.max", Integer.class));
		sysconf.setThreatLevelHighDueIn(parsePeriodProperty(env.getProperty("threatlevel.high.duein")));
		sysconf.setHeartbeatThreshold(env.getProperty("heartbeat.alivethreshold", Integer.class));

		return sysconf;
	}


	/**
	 * Parse a duration string from the properties file into a Java Duration object.
	 * The string must be of the format '[\\d+]d[\\d+]h[\\d+]m[\\d+]s' while every part is optional.
	 * The first part is for days, the second for hours, the third for minutes and the fourth for seconds.
	 * If the string is empty, the result will be equal to Duration.ZERO.<br>
	 * Some valid examples:<br>
	 * - 2d12h30m30s<br>
	 * - 12h30m<br>
	 * - 5d15s
	 *
	 * @param prop Duration string from the properties file
	 * @return Created Duration object
	 */
	private Duration parsePeriodProperty(String prop) {
		Pattern p = Pattern.compile("^(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$");
		Matcher m = p.matcher(prop);

		if(m.matches()) {
			Duration rv = Duration.ZERO;

			if(m.group(1) != null) {
				rv = rv.plusDays(Integer.parseInt(m.group(1)));
			}
			if(m.group(2) != null) {
				rv = rv.plusHours(Integer.parseInt(m.group(2)));
			}
			if(m.group(3) != null) {
				rv = rv.plusMinutes(Integer.parseInt(m.group(3)));
			}
			if(m.group(4) != null) {
				rv = rv.plusSeconds(Integer.parseInt(m.group(4)));
			}

			return rv;
		}
		else {
			throw new IllegalArgumentException("Invalid value for 'due in' property: " + prop);
		}
	}


	/**
	 * Class to hold the system configuration for later use.
	 * This includes the RT custom field used for determining SIEM access rights and the ticket queue name for SIEM tickets.
	 *
	 * @author Thomas Rix (rix@decoit.de)
	 */
	@Getter
	@Setter
	public static class SystemConfig {
		private String siemAccessCustomField;
		private String siemAdminAccessCustomField;
		private String siemTicketQueueName;
		private String incidentIdCustomField;
		private String incidentRiskCustomField;
		private int threatLevelLowMin;
		private int threatLevelLowMax;
		private Duration threatLevelLowDueIn;
		private int threatLevelMedMin;
		private int threatLevelMedMax;
		private Duration threatLevelMedDueIn;
		private int threatLevelHighMin;
		private int threatLevelHighMax;
		private Duration threatLevelHighDueIn;
		private int heartbeatThreshold;
	}
}
