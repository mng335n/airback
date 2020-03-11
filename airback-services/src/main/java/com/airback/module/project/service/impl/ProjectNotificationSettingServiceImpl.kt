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

import com.airback.common.NotificationType
import com.airback.core.cache.CacheKey
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import com.airback.module.project.dao.ProjectNotificationSettingMapper
import com.airback.module.project.dao.ProjectNotificationSettingMapperExt
import com.airback.module.project.domain.ProjectNotificationSetting
import com.airback.module.project.domain.ProjectNotificationSettingExample
import com.airback.module.project.service.ProjectNotificationSettingService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class ProjectNotificationSettingServiceImpl(private val projectNotificationSettingMapper: ProjectNotificationSettingMapper,
                                                 private val projectNotificationSettingMapperExt: ProjectNotificationSettingMapperExt) : DefaultCrudService<Int, ProjectNotificationSetting>(), ProjectNotificationSettingService {

    override val crudMapper: ICrudGenericDAO<Int, ProjectNotificationSetting>
        get() = projectNotificationSettingMapper as ICrudGenericDAO<Int, ProjectNotificationSetting>

    override fun findNotification(username: String, projectId: Int, @CacheKey sAccountId: Int): ProjectNotificationSetting {
        val ex = ProjectNotificationSettingExample()
        ex.createCriteria().andUsernameEqualTo(username).andProjectidEqualTo(projectId).andSaccountidEqualTo(sAccountId)
        val settings = projectNotificationSettingMapper.selectByExample(ex)
        return if (settings.isNotEmpty()) {
            settings[0]
        } else {
            val setting = ProjectNotificationSetting()
            setting.level = NotificationType.Default.name
            setting.projectid = projectId
            setting.saccountid = sAccountId
            setting.username = username
            setting
        }
    }

    override fun findNotifications(projectId: Int, @CacheKey sAccountId: Int): List<ProjectNotificationSetting> =
            projectNotificationSettingMapperExt.findNotifications(projectId, sAccountId)
}
