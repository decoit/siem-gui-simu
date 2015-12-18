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
import lombok.ToString;


/**
 * An outgoing message to send the current threat level summary.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@ToString
@EqualsAndHashCode
public class MsgThreatLevel {
	@JsonProperty("highRiskDisplay")
	private final long highRiskDisplay;

	@JsonProperty("mediumRiskDisplay")
	private final long mediumRiskDisplay;

	@JsonProperty("lowRiskDisplay")
	private final long lowRiskDisplay;


	public MsgThreatLevel(long highRisk, long mediumRisk) {
		if (highRisk >= 10) {
			highRiskDisplay = 100;
			mediumRiskDisplay = 0;
			lowRiskDisplay = 0;
		}
		else {
			highRiskDisplay = highRisk * 10;

			if (mediumRisk > (10 - highRisk)) {
				mediumRiskDisplay = (10 - highRisk) * 10;
				lowRiskDisplay = 0;
			}
			else {
				mediumRiskDisplay = mediumRisk * 10;
				lowRiskDisplay = (10 - highRisk - mediumRisk) * 10;
			}
		}
	}
}
