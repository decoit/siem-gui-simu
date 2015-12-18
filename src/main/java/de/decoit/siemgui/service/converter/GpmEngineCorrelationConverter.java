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
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.domain.ThreatLevel;
import de.decoit.siemgui.exception.ConversionException;
import de.decoit.simu.incidents.entities.IncidentEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class GpmEngineCorrelationConverter implements CorrelationConverter<IncidentEntity, de.decoit.simu.incidents.enums.IncidentStatus> {
	private final Logger LOG = LoggerFactory.getLogger(GpmEngineCorrelationConverter.class.getName());

	@Autowired
	private DateConverter dateConv;

	@Autowired
	private RootConfig.SystemConfig sysConf;


	@Override
	public Incident convertIncident(IncidentEntity inc) throws ConversionException {
		if(inc == null) {
			throw new ConversionException("Received null pointer for IncidentEntity conversion");
		}

//		try {
			Incident i = new Incident();

			i.setId(inc.getId());
			i.setTimestamp(dateConv.dateToLocalDateTime(inc.getTimestamp()));
			i.setName(inc.getName());

//			// Source address may be null, need to catch that case
//			InetAddress src;
//			if(inc.getSourceIp() != null) {
//				src = InetAddress.getByName(inc.getSourceIp());
//			}
//			else {
//				src = null;
//			}
//			i.setSource(src);
//
//			// Destination address may be null, need to catch that case
//			InetAddress dest;
//			if(inc.getDestinationIp() != null) {
//				dest = InetAddress.getByName(inc.getDestinationIp());
//			}
//			else {
//				dest = null;
//			}
//			i.setDestination(dest);

			i.setRisk(inc.getRisk());
			i.setThreatLevel(determineThreatLevel(inc.getRisk()));
			i.setStatus(convertIncidentStatus(inc.getStatus()));
			i.setDescription(inc.getDescription());
			i.setGraph(inc.getGraph());
			i.setRecommendation(inc.getRecommendation());
			i.setRuleId(inc.getRuleId());

			return i;
//		}
//		catch(UnknownHostException ex) {
//			throw new ConversionException("Invalid source or destination address", ex);
//		}
	}


	@Override
	public List<Incident> convertIncidentList(List<IncidentEntity> incList) throws ConversionException {
		if (incList == null) {
			throw new ConversionException("Received null pointer for List<IncidentEntity> conversion");
		}

		List<Incident> domList = incList.stream().map(i -> this.convertIncident(i)).collect(Collectors.toList());

		return domList;
	}


	/**
	 * {@inheritDoc}
	 *
	 * If the enum cannot be converted, the enum constant Unknown will be returned.
	 *
	 * @param status Input enum
	 * @return Converted enum
	 */
	@Override
	public de.decoit.simu.incidents.enums.IncidentStatus convertDomIncidentStatus(IncidentStatus status) {
		switch(status) {
			case NEW:
				return de.decoit.simu.incidents.enums.IncidentStatus.New;
			case IN_PROGRESS:
				return de.decoit.simu.incidents.enums.IncidentStatus.InProgress;
			case DONE:
				return de.decoit.simu.incidents.enums.IncidentStatus.Done;
			default:
				if(LOG.isDebugEnabled()) {
					LOG.debug("IncidentStatus (dom->spec) conversion hit default case with: " + status);
				}
				return de.decoit.simu.incidents.enums.IncidentStatus.Unknown;
		}
	}


	/**
	 * Convert the incident status enum of the correlation to the general incident status enum.
	 * If the enum cannot be converted, the enum constant Unknown will be returned.
	 *
	 * @param status Correlation incident status
	 * @return General incident status
	 */
	private IncidentStatus convertIncidentStatus(de.decoit.simu.incidents.enums.IncidentStatus status) {
		switch(status) {
			case New:
				return IncidentStatus.NEW;
			case InProgress:
				return IncidentStatus.IN_PROGRESS;
			case Done:
				return IncidentStatus.DONE;
			default:
				if(LOG.isDebugEnabled()) {
					LOG.debug("IncidentStatus conversion(spec->dom) hit default case with: " + status);
				}
				return IncidentStatus.UNKNOWN;
		}
	}


	/**
	 * Determine the threat level based on a risk value.
	 * This method uses the threat level limits defined in the configuration file.
	 *
	 * @param risk Risk value to determine threat level for
	 * @return Threat level
	 */
	private ThreatLevel determineThreatLevel(int risk) {
		if(risk >= sysConf.getThreatLevelLowMin() && risk <= sysConf.getThreatLevelLowMax()) {
			return ThreatLevel.LOW;
		}
		else if(risk >= sysConf.getThreatLevelMedMin() && risk <= sysConf.getThreatLevelMedMax()) {
			return ThreatLevel.MEDIUM;
		}
		else if(risk >= sysConf.getThreatLevelHighMin() && risk <= sysConf.getThreatLevelHighMax()) {
			return ThreatLevel.HIGH;
		}
		else {
			throw new ConversionException("Incident risk does not fit into configured threat level limits");
		}
	}
}
