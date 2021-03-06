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
package de.decoit.siemgui.stomp.msgs.outgoing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Objects of this class represent a STOMP message containing incident information for the overview page.
 * The object can be directly translated to and from JSON code using the Spring Messaging module.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MsgOverviewIncidents {
	@JsonProperty("incidentsNew")
	private long incidentsNew = 0;

	@JsonProperty("incidentsInProgress")
	private long incidentsInProgress = 0;

	@JsonProperty("incidentsUnknown")
	private long incidentsUnknown = 0;

	@JsonProperty("incidentsLowRisk")
	private long incidentsLowRisk = 0;

	@JsonProperty("incidentsMediumRisk")
	private long incidentsMediumRisk = 0;

	@JsonProperty("incidentsHighRisk")
	private long incidentsHighRisk = 0;

	@JsonProperty("error")
	private boolean error = false;
}
