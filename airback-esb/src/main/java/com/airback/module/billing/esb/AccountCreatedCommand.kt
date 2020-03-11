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
package com.airback.module.billing.esb

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.airback.common.NotificationType
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum
import com.airback.common.i18n.WikiI18nEnum
import com.airback.common.service.OptionValService
import com.airback.core.utils.BeanUtility
import com.airback.core.utils.StringUtils
import com.airback.module.esb.GenericCommand
import com.airback.module.file.PathUtils
import com.airback.module.page.domain.Folder
import com.airback.module.page.domain.Page
import com.airback.module.page.service.PageService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.*
import com.airback.module.project.i18n.OptionI18nEnum.*
import com.airback.module.project.service.*
import com.airback.module.project.domain.BugWithBLOBs
import com.airback.module.project.domain.Version
import com.airback.module.project.service.TicketRelationService
import com.airback.module.project.service.BugService
import com.airback.module.project.service.ComponentService
import com.airback.module.project.service.VersionService
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
class AccountCreatedCommand(private val optionValService: OptionValService,
                            private val projectService: ProjectService,
                            private val messageService: MessageService,
                            private val milestoneService: MilestoneService,
                            private val taskService: TaskService,
                            private val bugService: BugService,
                            private val bugRelatedService: TicketRelationService,
                            private val componentService: ComponentService,
                            private val versionService: VersionService,
                            private val pageService: PageService,
                            private val projectNotificationSettingService: ProjectNotificationSettingService) : GenericCommand() {


    @AllowConcurrentEvents
    @Subscribe
    fun execute(event: AccountCreatedEvent) {
        createDefaultOptionVals(event.accountId)
        if (event.createSampleData != null && event.createSampleData == true) {
            createSampleProjectData(event.initialUser, event.accountId)
        }
    }

    private fun createDefaultOptionVals(accountId: Int) {
        optionValService.createDefaultOptions(accountId)
    }

    private fun createSampleProjectData(initialUser: String, accountId: Int) {
        val nowDateTime = LocalDateTime.now()
        val nowDate = LocalDate.now()

        val project = Project()
        project.saccountid = accountId
        project.description = "Sample project"
        project.homepage = "https://www.airback.com"
        project.name = "Sample project"
        project.status = StatusI18nEnum.Open.name
        project.shortname = "SP1"
        val projectId = projectService.saveWithSession(project, initialUser)

        val projectNotificationSetting = ProjectNotificationSetting()
        projectNotificationSetting.level = NotificationType.None.name
        projectNotificationSetting.projectid = projectId
        projectNotificationSetting.saccountid = accountId
        projectNotificationSetting.username = initialUser
        projectNotificationSettingService.saveWithSession(projectNotificationSetting, initialUser)

        val message = Message()
        message.isstick = true
        message.createduser = initialUser
        message.message = "Welcome to airback workspace. I hope you enjoy it!"
        message.saccountid = accountId
        message.projectid = projectId
        message.title = "Thank you for using airback!"
        message.createdtime = nowDateTime
        messageService.saveWithSession(message, initialUser)

        val milestone = Milestone()
        milestone.createduser = initialUser
        milestone.duedate = nowDate.plusDays(14)
        milestone.startdate = nowDate
        milestone.enddate = nowDate.plusDays(14)
        milestone.name = "Sample milestone"
        milestone.assignuser = initialUser
        milestone.projectid = projectId
        milestone.saccountid = accountId
        milestone.status = MilestoneStatus.InProgress.name
        val sampleMilestoneId = milestoneService.saveWithSession(milestone, initialUser)

        val taskA = Task()
        taskA.name = "Task A"
        taskA.projectid = projectId
        taskA.createduser = initialUser
        taskA.percentagecomplete = 0.0
        taskA.priority = Priority.Medium.name
        taskA.saccountid = accountId
        taskA.status = StatusI18nEnum.Open.name
        taskA.startdate = nowDate
        taskA.enddate = nowDate.plusDays(3)
        val taskAId = taskService.saveWithSession(taskA, initialUser)

        val taskB = BeanUtility.deepClone(taskA)
        taskB.name = "Task B"
        taskB.id = null
        taskB.milestoneid = sampleMilestoneId
        taskB.startdate = nowDate.plusDays(2)
        taskB.enddate = nowDate.plusDays(4)
        taskService.saveWithSession(taskB, initialUser)

        val taskC = BeanUtility.deepClone(taskA)
        taskC.id = null
        taskC.name = "Task C"
        taskC.startdate = nowDate.plusDays(3)
        taskC.enddate = nowDate.plusDays(5)
        taskC.parenttaskid = taskAId
        taskService.saveWithSession(taskC, initialUser)

        val taskD = BeanUtility.deepClone(taskA)
        taskD.id = null
        taskD.name = "Task D"
        taskD.startdate = nowDate
        taskD.enddate = nowDate.plusDays(2)
        taskService.saveWithSession(taskD, initialUser)

        val component = com.airback.module.project.domain.Component()
        component.name = "Component 1"
        component.createduser = initialUser
        component.description = "Sample Component 1"
        component.status = StatusI18nEnum.Open.name
        component.projectid = projectId
        component.saccountid = accountId
        component.userlead = initialUser
        componentService.saveWithSession(component, initialUser)

        val version = Version()
        version.createduser = initialUser
        version.name = "Version 1"
        version.description = "Sample version"
        version.duedate = nowDate.plusDays(21)
        version.projectid = projectId
        version.saccountid = accountId
        version.status = StatusI18nEnum.Open.name
        versionService.saveWithSession(version, initialUser)

        val bugA = BugWithBLOBs()
        bugA.description = "Sample bug"
        bugA.environment = "All platforms"
        bugA.assignuser = initialUser
        bugA.duedate = nowDate.plusDays(2)
        bugA.createduser = initialUser
        bugA.milestoneid = sampleMilestoneId
        bugA.name = "Bug A"
        bugA.status = StatusI18nEnum.Open.name
        bugA.priority = Priority.Medium.name
        bugA.projectid = projectId
        bugA.saccountid = accountId
        val bugAId = bugService.saveWithSession(bugA, initialUser)

        val bugB = BeanUtility.deepClone(bugA)
        bugB.id = null
        bugB.name = "Bug B"
        bugB.status = StatusI18nEnum.Resolved.name
        bugB.resolution = BugResolution.CannotReproduce.name
        bugB.priority = Priority.Low.name
        bugService.saveWithSession(bugB, initialUser)

        bugRelatedService.saveAffectedVersionsOfTicket(bugAId, ProjectTypeConstants.BUG, listOf(version))
        bugRelatedService.saveComponentsOfTicket(bugAId, ProjectTypeConstants.BUG, listOf(component))

        val page = Page()
        page.subject = "Welcome to sample workspace"
        page.content = "I hope you enjoy airback!"
        page.path = "${PathUtils.getProjectDocumentPath(accountId, projectId)}/${StringUtils.generateSoftUniqueId()}"
        page.status = WikiI18nEnum.status_public.name
        pageService.savePage(page, initialUser)

        val folder = Folder()
        folder.name = "Requirements"
        folder.description = "Sample folder"
        folder.path = "${PathUtils.getProjectDocumentPath(accountId, projectId)}/${StringUtils.generateSoftUniqueId()}"
        pageService.createFolder(folder, initialUser)

        val timer = Timer("Set member notification")
        timer.schedule(object : TimerTask() {
            override fun run() {
                projectNotificationSetting.level = NotificationType.Default.name
                projectNotificationSettingService.updateWithSession(projectNotificationSetting, initialUser)
            }
        }, 90000)
    }
}