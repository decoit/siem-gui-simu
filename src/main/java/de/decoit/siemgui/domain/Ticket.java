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
package de.decoit.siemgui.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * An object of this class represents a ticket in the used ticket system.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Ticket {
	private long id;
	private TicketStatus status;
	private int priority;
	private String title;
	private String creator;
	private LocalDateTime createdOn;
	private User owner;
	private int risk;
	private List<String> requestors;
	private int timeWorked;
	private LocalDateTime resolvedOn;
	private long incidentId;
	private TicketQueue queue;
	private LocalDateTime dueOn;
}
