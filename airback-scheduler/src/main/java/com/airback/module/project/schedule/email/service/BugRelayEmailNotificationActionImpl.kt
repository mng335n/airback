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

import com.hp.gagawa.java.elements.A
import com.hp.gagawa.java.elements.Span
import com.hp.gagawa.java.elements.Text
import com.airback.common.MonitorTypeConstants
import com.airback.common.NotificationType
import com.airback.common.i18n.GenericI18Enum
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum
import com.airback.core.airbackException
import com.airback.core.utils.StringUtils
import com.airback.html.FormatUtils
import com.airback.html.FormatUtils.newA
import com.airback.html.FormatUtils.newImg
import com.airback.html.FormatUtils.newLink
import com.airback.html.LinkUtils
import com.airback.module.billing.UserStatusConstants
import com.airback.module.mail.MailUtils
import com.airback.module.project.ProjectLinkGenerator
import com.airback.module.project.ProjectResources
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.ProjectRelayEmailNotification
import com.airback.module.project.i18n.BugI18nEnum
import com.airback.module.project.i18n.MilestoneI18nEnum
import com.airback.module.project.i18n.OptionI18nEnum.*
import com.airback.module.project.service.MilestoneService
import com.airback.module.project.service.ProjectNotificationSettingService
import com.airback.module.project.domain.BugWithBLOBs
import com.airback.module.project.domain.SimpleBug
import com.airback.module.project.service.BugService
import com.airback.module.user.AccountLinkGenerator
import com.airback.module.user.domain.SimpleUser
import com.airback.module.user.service.UserService
import com.airback.schedule.email.ItemFieldMapper
import com.airback.schedule.email.MailContext
import com.airback.schedule.email.format.DateFieldFormat
import com.airback.schedule.email.format.FieldFormat
import com.airback.schedule.email.format.I18nFieldFormat
import com.airback.schedule.email.project.BugRelayEmailNotificationAction
import com.airback.spring.AppContextUtil
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class BugRelayEmailNotificationActionImpl(private val bugService: BugService,
                                          private val projectNotificationService: ProjectNotificationSettingService) : SendMailToFollowersAction<SimpleBug>(), BugRelayEmailNotificationAction {

    private val mapper = BugFieldNameMapper()

    override fun buildExtraTemplateVariables(context: MailContext<SimpleBug>) {
        val emailNotification = context.emailNotification

        val summary = "#${bean!!.ticketKey} - ${bean!!.name}"
        val summaryLink = ProjectLinkGenerator.generateBugPreviewFullLink(siteUrl, bean!!.projectShortName, bean!!.ticketKey!!)

        val avatarId = if (projectMember != null) projectMember!!.memberAvatarId else ""
        val userAvatar = LinkUtils.newAvatar(avatarId)

        val makeChangeUser = "${userAvatar.write()} ${emailNotification.changeByUserFullName}"
        val actionEnum = when (emailNotification.action) {
            MonitorTypeConstants.CREATE_ACTION -> BugI18nEnum.MAIL_CREATE_ITEM_HEADING
            MonitorTypeConstants.UPDATE_ACTION -> BugI18nEnum.MAIL_UPDATE_ITEM_HEADING
            MonitorTypeConstants.ADD_COMMENT_ACTION -> BugI18nEnum.MAIL_COMMENT_ITEM_HEADING
            else -> throw airbackException("Not support action ${emailNotification.action}")
        }

        contentGenerator.putVariable("projectName", bean!!.projectname!!)
        contentGenerator.putVariable("projectNotificationUrl", ProjectLinkGenerator.generateProjectSettingFullLink(siteUrl, bean!!.projectid))
        contentGenerator.putVariable("actionHeading", context.getMessage(actionEnum, makeChangeUser))
        contentGenerator.putVariable("name", summary)
        contentGenerator.putVariable("summaryLink", summaryLink)
    }

    override fun getBeanInContext(notification: ProjectRelayEmailNotification): SimpleBug? =
            bugService.findById(notification.typeid.toInt(), notification.saccountid)

    override fun getItemName(): String = StringUtils.trim(bean!!.name, 100)

    override fun getProjectName(): String = bean!!.projectname!!

    override fun getCreateSubject(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_CREATE_ITEM_SUBJECT,
            bean!!.projectname, context.changeByUserFullName, getItemName())

    override fun getCreateSubjectNotification(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_CREATE_ITEM_SUBJECT,
            projectLink(), userLink(context), bugLink())

    override fun getUpdateSubject(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_UPDATE_ITEM_SUBJECT,
            bean!!.projectname, context.changeByUserFullName, getItemName())

    override fun getUpdateSubjectNotification(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_UPDATE_ITEM_SUBJECT,
            projectLink(), userLink(context), bugLink())

    override fun getCommentSubject(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_COMMENT_ITEM_SUBJECT,
            bean!!.projectname, context.changeByUserFullName, getItemName())

    override fun getCommentSubjectNotification(context: MailContext<SimpleBug>): String = context.getMessage(BugI18nEnum.MAIL_COMMENT_ITEM_SUBJECT,
            projectLink(), userLink(context), bugLink())

    private fun projectLink() = A(ProjectLinkGenerator.generateProjectLink(bean!!.projectid)).appendText(bean!!.projectname).write()

    private fun userLink(context: MailContext<SimpleBug>) = A(AccountLinkGenerator.generateUserLink(context.user.username)).appendText(context.changeByUserFullName).write()

    private fun bugLink() = A(ProjectLinkGenerator.generateBugPreviewLink(bean!!.projectShortName, bean!!.ticketKey!!)).appendText(getItemName()).write()

    override fun getItemFieldMapper(): ItemFieldMapper = mapper

    override fun getListNotifyUsersWithFilter(notification: ProjectRelayEmailNotification): List<SimpleUser> {
        val notificationSettings = projectNotificationService.findNotifications(notification.projectId, notification.saccountid)
        var notifyUsers = notification.notifyUsers

        notificationSettings.forEach {
            if (NotificationType.None.name == it.level) {
                notifyUsers = notifyUsers.filter { notifyUser -> notifyUser.username != it.username }
            } else if (NotificationType.Minimal.name == it.level) {
                val findResult = notifyUsers.find { notifyUser -> notifyUser.username == it.username }
                if (findResult != null) {
                    val bug = bugService.findById(notification.typeid.toInt(), notification.saccountid)
                    if (bug != null && it.username == bug.assignuser) {
                        val prjMember = projectMemberService.getActiveUserOfProject(it.username,
                                it.projectid, it.saccountid)
                        if (prjMember != null && prjMember.status == UserStatusConstants.EMAIL_VERIFIED) {
                            notifyUsers = notifyUsers + prjMember
                        }
                    }
                }
            } else if (NotificationType.Full.name == it.level) {
                val prjMember = projectMemberService.getActiveUserOfProject(it.username, it.projectid, it.saccountid)
                if (prjMember != null && prjMember.status == UserStatusConstants.EMAIL_VERIFIED) notifyUsers = notifyUsers + prjMember
            }
        }
        return notifyUsers
    }

    override fun getType(): String = ProjectTypeConstants.BUG

    override fun getTypeId(): String = "${bean!!.id}"

    class BugFieldNameMapper : ItemFieldMapper() {
        init {
            put(BugWithBLOBs.Field.name, BugI18nEnum.FORM_SUMMARY, isColSpan = true)
            put(BugWithBLOBs.Field.environment, BugI18nEnum.FORM_ENVIRONMENT, isColSpan = true)
            put(BugWithBLOBs.Field.description, GenericI18Enum.FORM_DESCRIPTION, isColSpan = true)
            put(BugWithBLOBs.Field.assignuser, AssigneeFieldFormat(BugWithBLOBs.Field.assignuser.name, GenericI18Enum.FORM_ASSIGNEE))
            put(BugWithBLOBs.Field.milestoneid, MilestoneFieldFormat(BugWithBLOBs.Field.milestoneid.name, MilestoneI18nEnum.SINGLE))
            put(BugWithBLOBs.Field.status, I18nFieldFormat(BugWithBLOBs.Field.status.name, GenericI18Enum.FORM_STATUS, StatusI18nEnum::class.java))
            put(BugWithBLOBs.Field.resolution, I18nFieldFormat(BugWithBLOBs.Field.resolution.name, BugI18nEnum.FORM_RESOLUTION, BugResolution::class.java))
            put(BugWithBLOBs.Field.severity, I18nFieldFormat(BugWithBLOBs.Field.severity.name, BugI18nEnum.FORM_SEVERITY, BugSeverity::class.java))
            put(BugWithBLOBs.Field.priority, I18nFieldFormat(BugWithBLOBs.Field.priority.name, GenericI18Enum.FORM_PRIORITY,
                    Priority::class.java))
            put(BugWithBLOBs.Field.duedate, DateFieldFormat(BugWithBLOBs.Field.duedate.name, GenericI18Enum.FORM_DUE_DATE))
            put(BugWithBLOBs.Field.createduser, LogUserFieldFormat(BugWithBLOBs.Field.createduser.name, BugI18nEnum.FORM_LOG_BY))
        }
    }

    class MilestoneFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {

        override fun formatField(context: MailContext<*>): String {
            val bug = context.wrappedBean as SimpleBug
            return if (bug.milestoneid == null || bug.milestoneName == null) {
                Span().write()
            } else {
                val img = Text(ProjectResources.getFontIconHtml(ProjectTypeConstants.MILESTONE))
                val milestoneLink = ProjectLinkGenerator.generateMilestonePreviewFullLink(context.siteUrl,
                        bug.projectid, bug.milestoneid)
                val link = newA(milestoneLink, bug.milestoneName!!)
                FormatUtils.newLink(img, link).write()
            }
        }

        override fun formatField(context: MailContext<*>, value: String): String {
            return if (StringUtils.isBlank(value)) {
                Span().write()
            } else {
                val milestoneId = value.toInt()
                val milestoneService = AppContextUtil.getSpringBean(MilestoneService::class.java)
                val milestone = milestoneService.findById(milestoneId, context.saccountid)
                if (milestone != null) {
                    val img = Text(ProjectResources.getFontIconHtml(ProjectTypeConstants.MILESTONE))
                    val milestoneLink = ProjectLinkGenerator.generateMilestonePreviewFullLink(context.siteUrl,
                            milestone.projectid, milestone.id)
                    val link = newA(milestoneLink, milestone.name)
                    return newLink(img, link).write()
                } else ""
            }
        }
    }

    class AssigneeFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {

        override fun formatField(context: MailContext<*>): String {
            val bug = context.wrappedBean as SimpleBug
            return if (bug.assignuser != null) {
                val userAvatarLink = MailUtils.getAvatarLink(bug.assignUserAvatarId, 16)
                val img = newImg("avatar", userAvatarLink)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(bug.saccountid), bug.assignuser)
                val link = newA(userLink, bug.assignuserFullName)
                newLink(img, link).write()
            } else Span().write()
        }

        override fun formatField(context: MailContext<*>, value: String): String {
            return if (StringUtils.isBlank(value)) {
                Span().write()
            } else {
                val userService = AppContextUtil.getSpringBean(UserService::class.java)
                val user = userService.findUserByUserNameInAccount(value, context.saccountid)
                if (user != null) {
                    val userAvatarLink = MailUtils.getAvatarLink(user.avatarid, 16)
                    val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(context.saccountid), user.username)
                    val img = newImg("avatar", userAvatarLink)
                    val link = newA(userLink, user.displayName!!)
                    newLink(img, link).write()
                } else value
            }
        }
    }

    class LogUserFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {

        override fun formatField(context: MailContext<*>): String {
            val bug = context.wrappedBean as SimpleBug
            return if (bug.createduser != null) {
                val userAvatarLink = MailUtils.getAvatarLink(bug.loguserAvatarId, 16)
                val img = newImg("avatar", userAvatarLink)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(bug.saccountid), bug.createduser)
                val link = newA(userLink, bug.loguserFullName)
                newLink(img, link).write()
            } else Span().write()
        }

        override fun formatField(context: MailContext<*>, value: String): String {
            if (StringUtils.isBlank(value))
                return Span().write()

            val userService = AppContextUtil.getSpringBean(UserService::class.java)
            val user = userService.findUserByUserNameInAccount(value, context.saccountid)
            return if (user != null) {
                val userAvatarLink = MailUtils.getAvatarLink(user.avatarid, 16)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(context.saccountid), user.username)
                val img = newImg("avatar", userAvatarLink)
                val link = newA(userLink, user.displayName!!)
                newLink(img, link).write()
            } else value
        }
    }
}