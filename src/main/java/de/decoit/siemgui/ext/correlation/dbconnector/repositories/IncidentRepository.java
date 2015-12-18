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
package de.decoit.siemgui.ext.correlation.dbconnector.repositories;

import de.decoit.simu.incidents.entities.IncidentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {

	@Query("SELECT i FROM IncidentEntity i WHERE id > :lastid")
	public List<IncidentEntity> getNewIncidents(@Param("lastid") long lastId);

	@Query("SELECT count(i) FROM IncidentEntity i WHERE status <> 'Done'")
	public long countActiveIncidents();

	@Query("SELECT count(i) FROM IncidentEntity i WHERE status = 'Done'")
	public long countResolvedIncidents();

	@Query("SELECT count(i) FROM IncidentEntity i WHERE status <> 'Done' AND risk >= :lower AND risk <= :upper")
	public long countActiveIncidentsWithRiskRange(@Param("lower") int lower, @Param("upper") int upper);
}
