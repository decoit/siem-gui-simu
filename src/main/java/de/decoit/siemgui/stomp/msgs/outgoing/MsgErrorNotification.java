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
 * Outgoing message to notify the user of an occurred error.
 * The message can force a logout for the currently active user if a critical error happened.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@ToString
@EqualsAndHashCode
public class MsgErrorNotification {
	@JsonProperty("time")
	private final String time;

	@JsonProperty("text")
	private final String text;

	@JsonProperty("forceLogout")
	private final boolean forceLogout;


	/**
	 * Create a simple error notification with the specified message.
	 * It will not force a logout.
	 *
	 * @param text Error message
	 */
	public MsgErrorNotification(String text) {
		this(text, false);
	}


	/**
	 * Create an error message with the specified message and force logout behaviour.
	 *
	 * @param text        Error message
	 * @param forceLogout Force logout of the current user or not
	 */
	public MsgErrorNotification(String text, boolean forceLogout) {
		this.time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME);
		this.text = text;
		this.forceLogout = forceLogout;
	}
}
