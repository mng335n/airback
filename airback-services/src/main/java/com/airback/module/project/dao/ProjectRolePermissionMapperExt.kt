package com.airback.module.project.dao

import com.airback.module.project.domain.RolePermissionVal
import org.apache.ibatis.annotations.Param

/**
 * @author airback Ltd
 * @since 7.0.0
 */
interface ProjectRolePermissionMapperExt {

    fun findProjectsPermissions(@Param("username") username: String?,
                                @Param("projectIds") projectIds: List<Int>?,
                                @Param("sAccountId") sAccountId: Int): List<RolePermissionVal>

    fun findProjectPermission(@Param("username") username: String?,
                              @Param("projectId") projectId: Int,
                              @Param("sAccountId") sAccountId: Int): RolePermissionVal
}