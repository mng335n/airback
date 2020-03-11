/**
 * Copyright © airback
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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.module.project.dao

import com.airback.common.domain.GroupItem
import com.airback.db.persistence.ISearchableDAO
import com.airback.module.project.domain.SimpleBug
import com.airback.module.project.domain.criteria.BugSearchCriteria
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Mapper
interface BugMapperExt : ISearchableDAO<BugSearchCriteria> {

    fun getBugById(bugId: Int): SimpleBug?

    fun getStatusSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getPrioritySummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getAssignedDefectsSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getResolutionDefectsSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getReporterDefectsSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getVersionDefectsSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>

    fun getComponentDefectsSummary(@Param("searchCriteria") criteria: BugSearchCriteria): List<GroupItem>
}
