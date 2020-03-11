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
import com.airback.common.i18n.GenericI18Enum
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum
import com.airback.core.airbackException
import com.airback.core.utils.StringUtils
import com.airback.html.FormatUtils
import com.airback.html.LinkUtils
import com.airback.module.mail.MailUtils
import com.airback.module.project.ProjectLinkGenerator
import com.airback.module.project.ProjectResources
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.ProjectRelayEmailNotification
import com.airback.module.project.domain.Risk
import com.airback.module.project.domain.SimpleRisk
import com.airback.module.project.i18n.MilestoneI18nEnum
import com.airback.module.project.i18n.OptionI18nEnum.RiskConsequence
import com.airback.module.project.i18n.OptionI18nEnum.RiskProbability
import com.airback.module.project.i18n.RiskI18nEnum
import com.airback.module.project.service.MilestoneService
import com.airback.module.project.service.RiskService
import com.airback.module.user.AccountLinkGenerator
import com.airback.module.user.service.UserService
import com.airback.schedule.email.ItemFieldMapper
import com.airback.schedule.email.MailContext
import com.airback.schedule.email.format.DateFieldFormat
import com.airback.schedule.email.format.FieldFormat
import com.airback.schedule.email.format.I18nFieldFormat
import com.airback.schedule.email.project.ProjectRiskRelayEmailNotificationAction
import com.airback.spring.AppContextUtil
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
class ProjectRiskRelayEmailNotificationActionImpl : SendMailToAllMembersAction<SimpleRisk>(), ProjectRiskRelayEmailNotificationAction {
    @Autowired private lateinit var riskService: RiskService

    private val mapper = ProjectFieldNameMapper()

    override fun getItemName(): String = StringUtils.trim(bean!!.name, 100)

    override fun getProjectName(): String = bean!!.projectName

    override fun getCreateSubject(context: MailContext<SimpleRisk>): String = context.getMessage(
            RiskI18nEnum.MAIL_CREATE_ITEM_SUBJECT, bean!!.projectName, context.changeByUserFullName, getItemName())

    override fun getCreateSubjectNotification(context: MailContext<SimpleRisk>): String =
            context.getMessage(RiskI18nEnum.MAIL_CREATE_ITEM_SUBJECT, projectLink(), userLink(context), riskLink())

    override fun getUpdateSubject(context: MailContext<SimpleRisk>): String = context.getMessage(
            RiskI18nEnum.MAIL_UPDATE_ITEM_SUBJECT, bean!!.projectName, context.changeByUserFullName, getItemName())

    override fun getUpdateSubjectNotification(context: MailContext<SimpleRisk>): String =
            context.getMessage(RiskI18nEnum.MAIL_UPDATE_ITEM_SUBJECT, projectLink(), userLink(context), riskLink())

    override fun getCommentSubject(context: MailContext<SimpleRisk>): String = context.getMessage(
            RiskI18nEnum.MAIL_COMMENT_ITEM_SUBJECT, bean!!.projectName, context.changeByUserFullName, getItemName())

    override fun getCommentSubjectNotification(context: MailContext<SimpleRisk>): String =
            context.getMessage(RiskI18nEnum.MAIL_COMMENT_ITEM_SUBJECT, projectLink(), userLink(context), riskLink())

    private fun projectLink() = A(ProjectLinkGenerator.generateProjectLink(bean!!.projectid)).
            appendText(StringUtils.trim(bean!!.projectName, 50)).setType(bean!!.projectName).write()

    private fun userLink(context: MailContext<SimpleRisk>) = A(AccountLinkGenerator.generateUserLink(context.user.username)).
            appendText(StringUtils.trim(context.changeByUserFullName, 50)).write()

    private fun riskLink() = A(ProjectLinkGenerator.generateRiskPreviewLink(bean!!.projectShortName, bean!!.ticketKey)).appendText(getItemName()).write()

    override fun getItemFieldMapper(): ItemFieldMapper = mapper

    override fun getBeanInContext(notification: ProjectRelayEmailNotification): SimpleRisk? =
            riskService.findById(notification.typeid.toInt(), notification.saccountid)

    override fun getType(): String = ProjectTypeConstants.RISK

    override fun getTypeId(): String = "${bean!!.id}"

    override fun buildExtraTemplateVariables(context: MailContext<SimpleRisk>) {
        val emailNotification = context.emailNotification
        val summary = bean!!.name
        val summaryLink = ProjectLinkGenerator.generateRiskPreviewFullLink(siteUrl, bean!!.projectShortName, bean!!.ticketKey)

        val avatarId = if (projectMember != null) projectMember!!.memberAvatarId else ""
        val userAvatar = LinkUtils.newAvatar(avatarId)

        val makeChangeUser = "${userAvatar.write()} ${emailNotification.changeByUserFullName}"
        val actionEnum = when (emailNotification.action) {
            MonitorTypeConstants.CREATE_ACTION -> RiskI18nEnum.MAIL_CREATE_ITEM_HEADING
            MonitorTypeConstants.UPDATE_ACTION -> RiskI18nEnum.MAIL_UPDATE_ITEM_HEADING
            MonitorTypeConstants.ADD_COMMENT_ACTION -> RiskI18nEnum.MAIL_COMMENT_ITEM_HEADING
            else -> throw airbackException("Not support action ${emailNotification.action}")
        }

        contentGenerator.putVariable("projectName", bean!!.projectName)
        contentGenerator.putVariable("projectNotificationUrl", ProjectLinkGenerator.generateProjectSettingFullLink(siteUrl, bean!!.projectid))
        contentGenerator.putVariable("actionHeading", context.getMessage(actionEnum, makeChangeUser))
        contentGenerator.putVariable("name", summary)
        contentGenerator.putVariable("summaryLink", summaryLink)
    }

    class ProjectFieldNameMapper : ItemFieldMapper() {
        init {
            put(Risk.Field.name, GenericI18Enum.FORM_NAME, true)
            put(Risk.Field.description, GenericI18Enum.FORM_DESCRIPTION, true)
            put(Risk.Field.probability, I18nFieldFormat(Risk.Field.probability.name, RiskI18nEnum.FORM_PROBABILITY,
                    RiskProbability::class.java))
            put(Risk.Field.consequence, I18nFieldFormat(Risk.Field.consequence.name, RiskI18nEnum.FORM_CONSEQUENCE, RiskConsequence::class.java))
            put(Risk.Field.startdate, DateFieldFormat(Risk.Field.startdate.name, GenericI18Enum.FORM_START_DATE))
            put(Risk.Field.enddate, DateFieldFormat(Risk.Field.enddate.name, GenericI18Enum.FORM_END_DATE))
            put(Risk.Field.duedate, DateFieldFormat(Risk.Field.duedate.name, GenericI18Enum.FORM_DUE_DATE))
            put(Risk.Field.status, I18nFieldFormat(Risk.Field.status.name, GenericI18Enum.FORM_STATUS,
                    StatusI18nEnum::class.java))
            put(Risk.Field.milestoneid, MilestoneFieldFormat(Risk.Field.milestoneid.name, MilestoneI18nEnum.SINGLE))
            put(Risk.Field.assignuser, AssigneeFieldFormat(Risk.Field.assignuser.name, GenericI18Enum.FORM_ASSIGNEE))
            put(Risk.Field.createduser, RaisedByFieldFormat(Risk.Field.createduser.name, RiskI18nEnum.FORM_RAISED_BY))
            put(Risk.Field.response, RiskI18nEnum.FORM_RESPONSE, true)
        }
    }

    class AssigneeFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {
        override fun formatField(context: MailContext<*>): String {
            val risk = context.wrappedBean as SimpleRisk
            return if (risk.assignuser != null) {
                val userAvatarLink = MailUtils.getAvatarLink(risk.assignToUserAvatarId, 16)
                val img = FormatUtils.newImg("avatar", userAvatarLink)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(risk.saccountid),
                        risk.assignuser)
                val link = FormatUtils.newA(userLink, risk.assignedToUserFullName)
                FormatUtils.newLink(img, link).write()
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
                    val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(context.saccountid),
                            user.username)
                    val img = FormatUtils.newImg("avatar", userAvatarLink)
                    val link = FormatUtils.newA(userLink, user.displayName!!)
                    FormatUtils.newLink(img, link).write()
                } else value
            }
        }
    }

    class RaisedByFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {
        override fun formatField(context: MailContext<*>): String {
            val risk = context.wrappedBean as SimpleRisk
            return if (risk.createduser != null) {
                val userAvatarLink = MailUtils.getAvatarLink(risk.raisedByUserAvatarId, 16)
                val img = FormatUtils.newImg("avatar", userAvatarLink)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(risk.saccountid),
                        risk.createduser)
                val link = FormatUtils.newA(userLink, risk.raisedByUserFullName)
                FormatUtils.newLink(img, link).write()
            } else Span().write()
        }

        override fun formatField(context: MailContext<*>, value: String): String {
            if (StringUtils.isBlank(value)) {
                return Span().write()
            }
            val userService = AppContextUtil.getSpringBean(UserService::class.java)
            val user = userService.findUserByUserNameInAccount(value, context.saccountid)
            return if (user != null) {
                val userAvatarLink = MailUtils.getAvatarLink(user.avatarid, 16)
                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(MailUtils.getSiteUrl(context.saccountid),
                        user.username)
                val img = FormatUtils.newImg("avatar", userAvatarLink)
                val link = FormatUtils.newA(userLink, user.displayName!!)
                FormatUtils.newLink(img, link).write()
            } else value
        }
    }

    class MilestoneFieldFormat(fieldName: String, displayName: Enum<*>) : FieldFormat(fieldName, displayName) {

        override fun formatField(context: MailContext<*>): String {
            val risk = context.wrappedBean as SimpleRisk
            return if (risk.milestoneid != null) {
                val img = Text(ProjectResources.getFontIconHtml(ProjectTypeConstants.MILESTONE))
                val milestoneLink = ProjectLinkGenerator.generateMilestonePreviewFullLink(context.siteUrl, risk.projectid,
                        risk.milestoneid)
                val link = FormatUtils.newA(milestoneLink, risk.milestoneName!!)
                FormatUtils.newLink(img, link).write()
            } else Span().write()
        }

        override fun formatField(context: MailContext<*>, value: String): String {
            if (StringUtils.isBlank(value)) {
                return Span().write()
            }
            val milestoneId = value.toInt()
            val milestoneService = AppContextUtil.getSpringBean(MilestoneService::class.java)
            val milestone = milestoneService.findById(milestoneId, context.saccountid)
            return if (milestone != null) {
                val img = Text(ProjectResources.getFontIconHtml(ProjectTypeConstants.MILESTONE))
                val milestoneLink = ProjectLinkGenerator.generateMilestonePreviewFullLink(context.siteUrl,
                        milestone.projectid, milestone.id)
                val link = FormatUtils.newA(milestoneLink, milestone.name)
                return FormatUtils.newLink(img, link).write()
            } else value
        }
    }
}