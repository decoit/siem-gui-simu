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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * An object of this class represents a single incident read from the incident database.
 * A Ticket object can be attached to an Incident to allow tracking of incident's processing.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "id" })
public class Incident {
	private long id;
	private LocalDateTime timestamp;
	private IncidentStatus status;
	private String name;
//	private InetAddress source;
//	private String sourceName;
//	private InetAddress destination;
//	private String destinationName;
	private int risk;
	private String description;
	private long ruleId;
	private String graph;
	private String recommendation;
	private ThreatLevel threatLevel;
	private Ticket ticket;
//	private List<IncidentRecommendation> recommendations;

	// TODO: Domain object implementation missing
//	private List<ExplanationEntity> explanations;


	@JsonProperty("isOverdue")
	public boolean isOverdue() {
		if(this.ticket == null) {
			throw new IllegalStateException("No ticket attached to this incident");
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime due = this.ticket.getDueOn();

		return due.isBefore(now);
	}
}
