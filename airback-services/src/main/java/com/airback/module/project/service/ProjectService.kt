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
package com.airback.module.project.service

import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.project.domain.Project
import com.airback.module.project.domain.ProjectRelayEmailNotification
import com.airback.module.project.domain.SimpleProject
import com.airback.module.project.domain.criteria.ProjectSearchCriteria
import com.airback.module.user.domain.BillingAccount

/**
 * @author airback Ltd.
 * @since 1.0
 */
interface ProjectService : IDefaultService<Int, Project, ProjectSearchCriteria> {

    @Cacheable
    fun getProjectKeysUserInvolved(username: String?, @CacheKey sAccountId: Int): List<Int>

    @Cacheable
    fun getOpenProjectKeysUserInvolved(username: String?, @CacheKey sAccountId: Int): List<Int>

    @Cacheable
    fun getProjectsUserInvolved(username: String?, @CacheKey sAccountId: Int): List<SimpleProject>

    @Cacheable
    fun findById(projectId: Int, @CacheKey sAccountId: Int): SimpleProject?

    fun getTotalActiveProjectsOfInvolvedUsers(username: String, @CacheKey sAccountId: Int?): Int?

    @Cacheable
    fun getTotalActiveProjectsInAccount(@CacheKey sAccountId: Int): Int

    fun getAccountInfoOfProject(projectId: Int): BillingAccount

    fun findProjectRelayEmailNotifications(): List<ProjectRelayEmailNotification>

    fun savePlainProject(record: Project, username: String?): Int
}
