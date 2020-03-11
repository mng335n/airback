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
package com.airback.schedule.jobs

import com.airback.common.MonitorTypeConstants
import com.airback.common.dao.RelayEmailNotificationMapper
import com.airback.common.domain.RelayEmailNotificationExample
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.service.ProjectService
import com.airback.spring.AppContextUtil
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@DisallowConcurrentExecution
class ProjectSendingRelayEmailNotificationJob : GenericQuartzJobBean() {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectSendingRelayEmailNotificationJob::class.java)
    }

    @Autowired
    private lateinit var projectService: ProjectService

    @Autowired
    private lateinit var relayNotificationMapper: RelayEmailNotificationMapper

    override fun executeJob(context: JobExecutionContext) {
        val relayEmailNotifications = projectService.findProjectRelayEmailNotifications()
        relayEmailNotifications.forEach {
            try {
                val mailServiceCls = MailServiceMap.service(it.type)
                val emailNotificationAction = AppContextUtil.getSpringBean(mailServiceCls)

                when (it.action) {
                    MonitorTypeConstants.CREATE_ACTION -> emailNotificationAction.sendNotificationForCreateAction(it)
                    MonitorTypeConstants.UPDATE_ACTION -> emailNotificationAction.sendNotificationForUpdateAction(it)
                    MonitorTypeConstants.ADD_COMMENT_ACTION -> emailNotificationAction.sendNotificationForCommentAction(it)
                }
            } catch (e: Exception) {
                LOG.error("Error while sending scheduler command", e)
            }
        }
        val ex = RelayEmailNotificationExample()
        ex.createCriteria().andTypeIn(listOf(ProjectTypeConstants.BUG, ProjectTypeConstants.TASK,
                ProjectTypeConstants.MESSAGE, ProjectTypeConstants.MILESTONE,
                ProjectTypeConstants.RISK, ProjectTypeConstants.COMPONENT,
                ProjectTypeConstants.VERSION))
        relayNotificationMapper.deleteByExample(ex)
    }
}
