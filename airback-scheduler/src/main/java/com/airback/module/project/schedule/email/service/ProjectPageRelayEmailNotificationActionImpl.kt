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
package com.airback.module.project.schedule.email.service

import com.airback.core.utils.StringUtils
import com.airback.module.page.domain.Page
import com.airback.module.page.service.PageService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.ProjectRelayEmailNotification
import com.airback.schedule.email.ItemFieldMapper
import com.airback.schedule.email.MailContext
import com.airback.schedule.email.project.ProjectPageRelayEmailNotificationAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class ProjectPageRelayEmailNotificationActionImpl : SendMailToAllMembersAction<Page>(), ProjectPageRelayEmailNotificationAction {
    @Autowired private lateinit var pageService: PageService

    override fun getBeanInContext(notification: ProjectRelayEmailNotification): Page? =
            pageService.getPage(notification.typeid, "")

    override fun buildExtraTemplateVariables(context: MailContext<Page>) {}

    override fun getItemName(): String = StringUtils.trim(bean!!.subject, 100)

    override fun getProjectName(): String = ""

    override fun getCreateSubject(context: MailContext<Page>): String = ""

    override fun getCreateSubjectNotification(context: MailContext<Page>): String = ""

    override fun getUpdateSubject(context: MailContext<Page>): String = ""

    override fun getUpdateSubjectNotification(context: MailContext<Page>): String = ""

    override fun getCommentSubject(context: MailContext<Page>): String = ""

    override fun getCommentSubjectNotification(context: MailContext<Page>): String = ""

    override fun getItemFieldMapper(): ItemFieldMapper = ItemFieldMapper()

    override fun getType(): String = ProjectTypeConstants.PAGE

    override fun getTypeId(): String = bean!!.path
}