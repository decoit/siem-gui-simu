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
package de.decoit.siemgui.dao.incidents.filter;

import de.decoit.siemgui.domain.IncidentStatus;
import de.decoit.siemgui.service.converter.GpmEngineCorrelationConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * An implementation of the IncidentFilter that produces valid HQL code. The HQL code can be used to
 * search for incidents using Hibernate and the Graph-Pattern-Matching engine correlation database.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class GpmEngineCorrelationHqlIncidentFilter implements IncidentFilter {
	private final Queue<String> filterQueue = new LinkedList<>();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	GpmEngineCorrelationConverter correlationConv;


	@Override
	public IncidentFilter and() {
		filterQueue.add("AND");

		return this;
	}


	@Override
	public IncidentFilter or() {
		filterQueue.add("OR");

		return this;
	}


	@Override
	public IncidentFilter leftParenthesis() {
		filterQueue.add("(");

		return this;
	}


	@Override
	public IncidentFilter rightParenthesis() {
		filterQueue.add(")");

		return this;
	}


	@Override
	public IncidentFilter createdOnGreaterThan(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("inc.timestamp");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.GREATER_THAN);
		sb.append("'");
		sb.append(dateFormatter.format(value));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter createdOnIs(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("inc.timestamp");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS);
		sb.append("'");
		sb.append(dateFormatter.format(value));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter createdOnLessThan(LocalDateTime value) {
		StringBuilder sb = new StringBuilder("inc.timestamp");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.LESS_THAN);
		sb.append("'");
		sb.append(dateFormatter.format(value));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter idGreaterThan(long value) {
		StringBuilder sb = new StringBuilder("inc.id");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter idIs(long value) {
		StringBuilder sb = new StringBuilder("inc.id");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter idIsNot(long value) {
		StringBuilder sb = new StringBuilder("inc.id");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter idLessThan(long value) {
		StringBuilder sb = new StringBuilder("inc.id");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter statusIs(IncidentStatus value) {
		StringBuilder sb = new StringBuilder("inc.status");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS);
		sb.append("'");
		sb.append(correlationConv.convertDomIncidentStatus(value));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter statusIsNot(IncidentStatus value) {
		StringBuilder sb = new StringBuilder("inc.status");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS_NOT);
		sb.append("'");
		sb.append(correlationConv.convertDomIncidentStatus(value));
		sb.append("'");

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter riskIs(int value) {
		StringBuilder sb = new StringBuilder("inc.risk");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter riskIsNot(int value) {
		StringBuilder sb = new StringBuilder("inc.risk");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.IS_NOT);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter riskLessThan(int value) {
		StringBuilder sb = new StringBuilder("inc.risk");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.LESS_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	@Override
	public IncidentFilter riskGreaterThan(int value) {
		StringBuilder sb = new StringBuilder("inc.risk");
		sb.append(GpmEngineCorrelationHqlIncidentFilter.HqlOperator.GREATER_THAN);
		sb.append(value);

		filterQueue.add(sb.toString());

		return this;
	}


	/**
	 * Convert the stored filter criteria into a string.
	 * The string will contain a HQL statement to search the correlation database using Hibernate.
	 *
	 * @return HQL statement
	 */
	@Override
	public String toNativeFilter() {
		StringBuilder sb = new StringBuilder("FROM de.decoit.simu.incidents.entities.IncidentEntity AS inc");

		if (!filterQueue.isEmpty()) {
			sb.append(" WHERE ");
			String s = filterQueue.poll();
			while (s != null) {
				sb.append(s);
				sb.append(" ");

				s = filterQueue.poll();
			}
		}

		return sb.toString().trim();
	}


	/**
	 * Defines the binary operators supported by HQL.
	 */
	private enum HqlOperator {
		IS(" = "),
		IS_NOT(" <> "),
		LESS_THAN(" < "),
		GREATER_THAN(" > "),
		LIKE(" LIKE "),
		NOT_LIKE(" NOT LIKE ");

		private final String operatorString;


		private HqlOperator(String opStr) {
			this.operatorString = opStr;
		}


		@Override
		public String toString() {
			return this.operatorString;
		}
	}
}
