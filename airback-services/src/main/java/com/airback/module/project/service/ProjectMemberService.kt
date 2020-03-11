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
package com.airback.module.project.service

import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.project.domain.ProjectMember
import com.airback.module.project.domain.SimpleProjectMember
import com.airback.module.project.domain.criteria.ProjectMemberSearchCriteria
import com.airback.module.user.domain.SimpleUser
import java.time.LocalDate

/**
 * @author airback Ltd.
 * @since 1.0
 */
interface ProjectMemberService : IDefaultService<Int, ProjectMember, ProjectMemberSearchCriteria> {

    @Cacheable
    fun findById(memberId: Int, @CacheKey sAccountId: Int): SimpleProjectMember?

    @Cacheable
    fun isUserBelongToProject(username: String, projectId: Int, @CacheKey sAccountId: Int): Boolean

    @Cacheable
    fun findMemberByUsername(username: String, projectId: Int, @CacheKey sAccountId: Int): SimpleProjectMember?

    @Cacheable
    fun getActiveUserOfProject(username: String, projectId: Int, @CacheKey sAccountId: Int): SimpleUser?

    @Cacheable
    fun getUsersNotInProject(projectId: Int?, @CacheKey sAccountId: Int?): List<SimpleUser>

    @Cacheable
    fun getActiveUsersInProject(projectId: Int?, @CacheKey sAccountId: Int?): List<SimpleUser>

    @Cacheable
    fun getActiveUsersInProjects(projectIds: List<Int>, @CacheKey sAccountId: Int?): List<SimpleUser>

    fun inviteProjectMembers(email: Array<String>, projectId: Int, projectRoleId: Int,
                             inviteUser: String, inviteMessage: String, sAccountId: Int)

    fun findMembersHourlyInProject(projectId: Int?, sAccountId: Int?, start: LocalDate, end: LocalDate): List<SimpleProjectMember>
}
