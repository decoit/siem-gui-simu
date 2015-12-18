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
package de.decoit.siemgui.dao.incidents;

import de.decoit.siemgui.ext.correlation.dbconnector.services.CorrelationDbConnector;
import de.decoit.siemgui.dao.filter.OrderByDirection;
import de.decoit.siemgui.dao.incidents.filter.IncidentFields;
import de.decoit.siemgui.dao.incidents.filter.IncidentFilter;
import de.decoit.siemgui.domain.Incident;
import de.decoit.siemgui.exception.ExternalServiceException;
import de.decoit.siemgui.service.converter.GpmEngineCorrelationConverter;
import de.decoit.simu.incidents.entities.IncidentEntity;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


/**
 * Implementation of IncidentDao interface to access the security incidents by using Hibernate and the correlation database.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Service
public class GpmEngineCorrelationDbIncidentDao implements IncidentDao {
	private final Logger LOG = LoggerFactory.getLogger(GpmEngineCorrelationDbIncidentDao.class.getName());

	@Autowired
	private CorrelationDbConnector incidentConnector;

	@Autowired
	private GpmEngineCorrelationConverter correlationObjConv;


	@Override
	public Incident getIncident(long id) throws ExternalServiceException {
		try {
			IncidentEntity incEntity = incidentConnector.getIncidentById(id);

			if (incEntity == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Incident entity not found! ID: " + id);
				}

				return null;
			}
			else {
				return correlationObjConv.convertIncident(incEntity);
			}
		}
		catch (DataAccessException ex) {
			throw new ExternalServiceException("Accessing the correlation database failed", ex);
		}
	}


	@Override
	public List<Incident> getAllIncidents() throws ExternalServiceException {
		List<IncidentEntity> ieList = incidentConnector.getAllIncidents();

		return correlationObjConv.convertIncidentList(ieList);
	}


	@Override
	public List<Incident> getNewIncidents(long lastId) throws ExternalServiceException {
		List<IncidentEntity> ieList = incidentConnector.getNewIncidents(lastId);

		return correlationObjConv.convertIncidentList(ieList);
	}


	@Override
	public List<Incident> searchIncidents(IncidentFilter filter, IncidentFields orderByField, OrderByDirection orderDirection) throws ExternalServiceException {
//		String hql = (String) filter.toNativeFilter();

		// TODO: Dirty, directly accessing the EntityManager is really dirty
//		List<IncidentEntity> results = em.createQuery(hql, IncidentEntity.class).getResultList();
		//List<IncidentEntity> results = getCurrentSession().createQuery(hql).list();

//		if (LOG.isDebugEnabled()) {
//			LOG.debug("Executed HQL: " + hql);
//			LOG.debug("Search incident results: " + results.size());
//		}

//		List<Incident> returnList = results.stream().map(t -> correlationObjConv.convertIncident(t)).collect(Collectors.toList());

//		return returnList;
		return new ArrayList<>();
	}


	@Override
	public Incident updateIncident(Incident inInc) throws ExternalServiceException {
		try {
			IncidentEntity ie = incidentConnector.getIncidentById(inInc.getId());

			if (ie != null) {
				ie.setStatus(correlationObjConv.convertDomIncidentStatus(inInc.getStatus()));

				IncidentEntity newIe = incidentConnector.saveAndFlush(ie);
				return correlationObjConv.convertIncident(newIe);
			}
			else {
				throw new ExternalServiceException("Incident for update not found! ID: " + inInc.getId());
			}
		}
		catch (DataAccessException ex) {
			throw new ExternalServiceException("Accessing the correlation database failed", ex);
		}
	}
}
