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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.project.dao

import com.airback.common.domain.criteria.ActivityStreamSearchCriteria
import com.airback.common.domain.criteria.MonitorSearchCriteria
import com.airback.db.persistence.ISearchableDAO
import com.airback.module.project.domain.FollowingTicket
import com.airback.module.project.domain.ProjectActivityStream
import com.airback.module.project.domain.ProjectRelayEmailNotification
import com.airback.module.project.domain.SimpleProject
import com.airback.module.project.domain.criteria.ProjectSearchCriteria
import com.airback.module.user.domain.BillingAccount
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.session.RowBounds

/**
 * @author airback Ltd
 * @since 1.0.0
 */
@Mapper
interface ProjectMapperExt : ISearchableDAO<ProjectSearchCriteria> {

    fun getTotalActivityStream(@Param("searchCriteria") criteria: ActivityStreamSearchCriteria): Int

    fun getProjectActivityStreams(@Param("searchCriteria") criteria: ActivityStreamSearchCriteria, rowBounds: RowBounds): List<ProjectActivityStream>

    fun getUserProjectKeys(@Param("searchCriteria") criteria: ProjectSearchCriteria): List<Int>

    fun getProjectsUserInvolved(@Param("username") username: String?, @Param("sAccountId") sAccountId: Int?): List<SimpleProject>

    fun findProjectById(projectId: Int): SimpleProject

    fun getAccountInfoOfProject(projectId: Int): BillingAccount

    fun getTotalFollowingTickets(@Param("searchCriteria") searchRequest: MonitorSearchCriteria): Int

    fun getProjectFollowingTickets(@Param("searchCriteria") searchRequest: MonitorSearchCriteria, rowBounds: RowBounds): List<FollowingTicket>

    fun findProjectRelayEmailNotifications(): List<ProjectRelayEmailNotification>
}
