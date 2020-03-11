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

import com.airback.core.Tuple2
import com.airback.core.cache.CacheEvict
import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.project.domain.ProjectRole
import com.airback.module.project.domain.SimpleProjectRole
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria
import com.airback.security.PermissionMap

/**
 * @author airback Ltd
 * @since 1.0.0
 */
interface ProjectRoleService : IDefaultService<Int, ProjectRole, ProjectRoleSearchCriteria> {

    @CacheEvict
    fun savePermission(projectId: Int, roleId: Int?, permissionMap: PermissionMap, @CacheKey sAccountId: Int)

    @Cacheable
    fun findById(roleId: Int, @CacheKey sAccountId: Int): SimpleProjectRole?

    @Cacheable
    fun findProjectsPermissions(username: String?, projectIds: List<Int>?, @CacheKey sAccountId: Int): List<Tuple2<Int, PermissionMap>>

    @Cacheable
    fun findProjectsPermission(username: String?, projectId: Int, @CacheKey sAccountId: Int): PermissionMap
}
