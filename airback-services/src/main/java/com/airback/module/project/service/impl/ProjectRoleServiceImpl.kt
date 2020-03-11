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
package com.airback.module.project.service.impl

import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.common.ModuleNameConstants
import com.airback.core.Tuple2
import com.airback.core.utils.JsonDeSerializer
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.ProjectRoleMapper
import com.airback.module.project.dao.ProjectRoleMapperExt
import com.airback.module.project.dao.ProjectRolePermissionMapper
import com.airback.module.project.dao.ProjectRolePermissionMapperExt
import com.airback.module.project.domain.ProjectRole
import com.airback.module.project.domain.ProjectRolePermission
import com.airback.module.project.domain.ProjectRolePermissionExample
import com.airback.module.project.domain.SimpleProjectRole
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria
import com.airback.module.project.service.ProjectRoleService
import com.airback.security.PermissionMap
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class ProjectRoleServiceImpl(private val roleMapper: ProjectRoleMapper,
                             private val roleMapperExt: ProjectRoleMapperExt,
                             private val rolePermissionMapper: ProjectRolePermissionMapper,
                             private val rolePermissionMapperExt: ProjectRolePermissionMapperExt) : DefaultService<Int, ProjectRole, ProjectRoleSearchCriteria>(), ProjectRoleService {

    override val crudMapper: ICrudGenericDAO<Int, ProjectRole>
        get() = roleMapper as ICrudGenericDAO<Int, ProjectRole>

    override val searchMapper: ISearchableDAO<ProjectRoleSearchCriteria>
        get() = roleMapperExt

    override fun savePermission(projectId: Int, roleId: Int?, permissionMap: PermissionMap, sAccountId: Int) {
        val perVal = JsonDeSerializer.toJson(permissionMap)

        val ex = ProjectRolePermissionExample()
        ex.createCriteria().andRoleidEqualTo(roleId)

        val rolePer = ProjectRolePermission()
        rolePer.roleid = roleId
        rolePer.projectid = projectId
        rolePer.roleval = perVal

        val data = rolePermissionMapper.countByExample(ex)
        when {
            data > 0 -> rolePermissionMapper.updateByExampleSelective(rolePer, ex)
            else -> rolePermissionMapper.insert(rolePer)
        }
    }

    override fun findById(roleId: Int, sAccountId: Int): SimpleProjectRole? = roleMapperExt.findRoleById(roleId)

    override fun findProjectsPermissions(username: String?, projectIds: List<Int>?, sAccountId: Int): List<Tuple2<Int, PermissionMap>> {
        val permissions = rolePermissionMapperExt.findProjectsPermissions(username, projectIds, sAccountId)
        return permissions.map {
            val permissionVal = it.permissionVal
            val permissionMap = PermissionMap.fromJsonString(permissionVal)
            Tuple2(it.roleId, permissionMap)
        }.toCollection(mutableListOf())
    }

    override fun findProjectsPermission(username: String?, projectId: Int, sAccountId: Int): PermissionMap {
        val rolePerVal = rolePermissionMapperExt.findProjectPermission(username, projectId, sAccountId);
        return PermissionMap.fromJsonString(rolePerVal.permissionVal)
    }

    companion object {

        init {
            ClassInfoMap.put(ProjectRoleServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.PROJECT_ROLE))
        }
    }
}
