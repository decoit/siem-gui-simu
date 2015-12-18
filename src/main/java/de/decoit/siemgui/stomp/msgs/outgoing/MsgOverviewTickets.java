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
 * Objects of this class represent a STOMP message containing ticket information for the overview page.
 * The object can be directly translated to and from JSON code using the Spring Messaging module.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MsgOverviewTickets {
	@JsonProperty("ticketsNew")
	private int ticketsNew = 0;

	@JsonProperty("ticketsOpen")
	private int ticketsOpen = 0;

	@JsonProperty("ticketsResolved")
	private int ticketsResolved = 0;

	@JsonProperty("myTicketsNew")
	private int myTicketsNew = 0;

	@JsonProperty("myTicketsOpen")
	private int myTicketsOpen = 0;

	@JsonProperty("myTicketsResolved")
	private int myTicketsResolved = 0;

	@JsonProperty("error")
	private boolean error = false;
}
