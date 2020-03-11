/**
 * Copyright © airback
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.module.project.view.ticket

import com.google.common.collect.Sets.newHashSet
import com.airback.common.TableViewField
import com.airback.db.query.VariableInjector
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.ProjectTicket
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria
import com.airback.module.project.fielddef.TicketTableFieldDef
import com.airback.module.project.fielddef.TicketTableFieldDef.assignee
import com.airback.module.project.fielddef.TicketTableFieldDef.billableHours
import com.airback.module.project.fielddef.TicketTableFieldDef.duedate
import com.airback.module.project.fielddef.TicketTableFieldDef.enddate
import com.airback.module.project.fielddef.TicketTableFieldDef.logUser
import com.airback.module.project.fielddef.TicketTableFieldDef.milestoneName
import com.airback.module.project.fielddef.TicketTableFieldDef.name
import com.airback.module.project.fielddef.TicketTableFieldDef.nonBillableHours
import com.airback.module.project.fielddef.TicketTableFieldDef.priority
import com.airback.module.project.fielddef.TicketTableFieldDef.startdate
import com.airback.module.project.i18n.OptionI18nEnum.Priority
import com.airback.module.project.i18n.TicketI18nEnum
import com.airback.module.project.service.ProjectTicketService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.reporting.CustomizeReportOutputWindow
import java.time.LocalDateTime

/**
 * @author airback Ltd
 * @since 5.3.4
 */
class TicketCustomizeReportOutputWindow(variableInjector: VariableInjector<ProjectTicketSearchCriteria>) :
        CustomizeReportOutputWindow<ProjectTicketSearchCriteria, ProjectTicket>(ProjectTypeConstants.TICKET,
                UserUIContext.getMessage(TicketI18nEnum.LIST), ProjectTicket::class.java,
                AppContextUtil.getSpringBean(ProjectTicketService::class.java), variableInjector) {

    override fun getSampleMap(): Map<String, String> = mapOf(
            name.field to "Task A",
            startdate.field to UserUIContext.formatDate(LocalDateTime.now().minusDays(2)),
            enddate.field to UserUIContext.formatDate(LocalDateTime.now()),
            duedate.field to UserUIContext.formatDate(LocalDateTime.now().plusDays(1)),
            priority.field to Priority.High.name,
            logUser.field to "Will Smith",
            assignee.field to "John Adams",
            milestoneName.field to "Milestone 1",
            billableHours.field to "3",
            nonBillableHours.field to "1",
            TicketTableFieldDef.description.field to "Description 1"
    )

    override fun getDefaultColumns(): Set<TableViewField> =
            newHashSet(name, startdate, duedate, priority, assignee, billableHours, nonBillableHours)

    override fun getAvailableColumns(): Set<TableViewField> =
            newHashSet(name, TicketTableFieldDef.description, startdate, enddate, duedate, priority, logUser,
                    assignee, milestoneName, billableHours, nonBillableHours)
}
