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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * Objects of this class represent a STOMP push notification to all active users.
 * The object can be directly translated to JSON code using the Spring Messaging module.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@ToString
@EqualsAndHashCode
public class MsgPushNotification {
	@JsonProperty("time")
	private final String time;

	@JsonProperty("text")
	private final String text;


	public MsgPushNotification(String text) {
		this.time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);
		this.text = text;
	}
}
