package com.airback.module.project.dao

import com.airback.module.project.domain.ProjectNotificationSetting
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ProjectNotificationSettingMapperExt {
    fun findNotifications(@Param("projectId") projectId: Int, @Param("sAccountId") sAccountId: Int): List<ProjectNotificationSetting>
}