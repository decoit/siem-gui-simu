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
package de.decoit.siemgui.dao.tickets.filter;

import de.decoit.siemgui.config.RootConfig.SystemConfig;
import de.decoit.siemgui.domain.TicketStatus;
import de.decoit.siemgui.domain.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Implementation of TicketFilter to be used for searching tickets via the RT Connector.
 * It creates a search string following the scheme of the RT query builder. After building the query string
 * the internal queue is empty and thus a second attempt to build the query will result in an empty string!
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtTicketFilter implements TicketFilter {
	@Autowired
	private SystemConfig sysConf;

	private final Queue<String> filterQueue = new LinkedList<>();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	@Override
	public TicketFilter and() {
		filterQueue.add("AND");

		return this;
	}


	@Override
	public TicketFilter or() {
		filterQueue.add("OR");

		return this;
	}


	@Override
	public TicketFilter leftParenthesis() {
		filterQueue.add("(");

		return this;
	}


	@Override
	public TicketFilter rightParenthesis() {
		filterQueue.add(")");

		return this;
	}


	@Override
	public TicketFilter idIs(long value) {
		StringBuilder sb = new StringBuilder("id");
		sb.append(RtOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter idIsNot(long value) {
		StringBuilder sb = new StringBuilder("id");
		sb.append(RtOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter idLessThan(long value) {
		StringBuilder sb = new StringBuilder("id");
		sb.append(RtOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter idGreaterThan(long value) {
		StringBuilder sb = new StringBuilder("id");
		sb.append(RtOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter queueIs(String value) {
		StringBuilder sb = new StringBuilder("Queue");
		sb.append(RtOperator.IS);
		sb.append("'");
		sb.append(value);
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter queueIsNot(String value) {
		StringBuilder sb = new StringBuilder("Queue");
		sb.append(RtOperator.IS_NOT);
		sb.append("'");
		sb.append(value);
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter subjectContains(String value) {
		StringBuilder sb = new StringBuilder("Subject");
		sb.append(RtOperator.LIKE);
		sb.append("'");
		sb.append(value);
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter subjectDoesNotContain(String value) {
		StringBuilder sb = new StringBuilder("Subject");
		sb.append(RtOperator.NOT_LIKE);
		sb.append("'");
		sb.append(value);
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter ownerIs(User value) {
		StringBuilder sb = new StringBuilder("Owner");
		sb.append(RtOperator.IS);
		sb.append("'");
		sb.append(value.getUsername());
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter ownerIsNot(User value) {
		StringBuilder sb = new StringBuilder("Owner");
		sb.append(RtOperator.IS_NOT);
		sb.append("'");
		sb.append(value.getUsername());
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter statusIs(TicketStatus value) {
		StringBuilder sb = new StringBuilder("Status");
		sb.append(RtOperator.IS);
		sb.append("'");
		sb.append(value.toString());
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter statusIsNot(TicketStatus value) {
		StringBuilder sb = new StringBuilder("Status");
		sb.append(RtOperator.IS_NOT);
		sb.append("'");
		sb.append(value.toString());
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter createdOnIs(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("Status");
		sb.append(RtOperator.IS);
		sb.append("'");
		sb.append(value.format(dateFormatter));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter createdOnLessThan(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("Status");
		sb.append(RtOperator.LESS_THAN);
		sb.append("'");
		sb.append(value.format(dateFormatter));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter createdOnGreaterThan(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("Status");
		sb.append(RtOperator.GREATER_THAN);
		sb.append("'");
		sb.append(value.format(dateFormatter));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter timeWorkedIs(int value) {
		StringBuilder sb = new StringBuilder("TimeWorked");
		sb.append(RtOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter timeWorkedIsNot(int value) {
		StringBuilder sb = new StringBuilder("TimeWorked");
		sb.append(RtOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter timeWorkedLessThan(int value) {
		StringBuilder sb = new StringBuilder("TimeWorked");
		sb.append(RtOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter timeWorkedGreaterThan(int value) {
		StringBuilder sb = new StringBuilder("TimeWorked");
		sb.append(RtOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter priorityIs(int value) {
		StringBuilder sb = new StringBuilder("Priority");
		sb.append(RtOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter priorityIsNot(int value) {
		StringBuilder sb = new StringBuilder("Priority");
		sb.append(RtOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter priorityLessThan(int value) {
		StringBuilder sb = new StringBuilder("Priority");
		sb.append(RtOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter priorityGreaterThan(int value) {
		StringBuilder sb = new StringBuilder("Priority");
		sb.append(RtOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter incidentIs(long value) {
		StringBuilder sb = new StringBuilder("CF.{");
		sb.append(sysConf.getIncidentIdCustomField());
		sb.append("}");
		sb.append(RtOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter riskIs(int value) {
		StringBuilder sb = new StringBuilder("CF.{");
		sb.append(sysConf.getIncidentRiskCustomField());
		sb.append("}");
		sb.append(RtOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter riskIsNot(int value) {
		StringBuilder sb = new StringBuilder("CF.{");
		sb.append(sysConf.getIncidentRiskCustomField());
		sb.append("}");
		sb.append(RtOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter riskLessThan(int value) {
		StringBuilder sb = new StringBuilder("CF.{");
		sb.append(sysConf.getIncidentRiskCustomField());
		sb.append("}");
		sb.append(RtOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public TicketFilter riskGreaterThan(int value) {
		StringBuilder sb = new StringBuilder("CF.{");
		sb.append(sysConf.getIncidentRiskCustomField());
		sb.append("}");
		sb.append(RtOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	/**
	 * Convert the stored filter criteria into a string.
	 * The string will follow the format defined by the RT query builder and can be used as value
	 * for the query parameter in RT REST API search requests.
	 *
	 * @return Query string
	 */
	@Override
	public String toNativeFilter() {
		StringBuilder sb = new StringBuilder();

		String s = filterQueue.poll();
		while (s != null) {
			sb.append(s);
			sb.append(" ");

			s = filterQueue.poll();
		}

		return sb.toString().trim();
	}


	@Override
	public int hashCode() {
		int hash = 3;
		return hash;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RtTicketFilter other = (RtTicketFilter) obj;
		if (!Objects.equals(this.filterQueue, other.filterQueue)) {
			return false;
		}
		return true;
	}


	/**
	 * Defines the binary operators supported by RT.
	 */
	private enum RtOperator {
		IS(" = "),
		IS_NOT(" != "),
		LESS_THAN(" < "),
		GREATER_THAN(" > "),
		LIKE(" LIKE "),
		NOT_LIKE(" NOT LIKE ");

		private final String operatorString;


		private RtOperator(String opStr) {
			this.operatorString = opStr;
		}


		@Override
		public String toString() {
			return this.operatorString;
		}
	}
}
