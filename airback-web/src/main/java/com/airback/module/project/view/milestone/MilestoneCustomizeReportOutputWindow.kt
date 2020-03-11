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
package com.airback.module.project.view.milestone

import com.google.common.collect.Sets.newHashSet
import com.airback.common.TableViewField
import com.airback.db.query.VariableInjector
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.SimpleMilestone
import com.airback.module.project.domain.criteria.MilestoneSearchCriteria
import com.airback.module.project.fielddef.MilestoneTableFieldDef.assignee
import com.airback.module.project.fielddef.MilestoneTableFieldDef.billableHours
import com.airback.module.project.fielddef.MilestoneTableFieldDef.endDate
import com.airback.module.project.fielddef.MilestoneTableFieldDef.milestoneName
import com.airback.module.project.fielddef.MilestoneTableFieldDef.nonBillableHours
import com.airback.module.project.fielddef.MilestoneTableFieldDef.startDate
import com.airback.module.project.fielddef.MilestoneTableFieldDef.status
import com.airback.module.project.i18n.MilestoneI18nEnum
import com.airback.module.project.i18n.OptionI18nEnum.MilestoneStatus
import com.airback.module.project.service.MilestoneService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.reporting.CustomizeReportOutputWindow
import java.time.LocalDateTime

/**
 * @author airback Ltd
 * @since 5.3.4
 */
class MilestoneCustomizeReportOutputWindow(variableInjector: VariableInjector<MilestoneSearchCriteria>) :
        CustomizeReportOutputWindow<MilestoneSearchCriteria, SimpleMilestone>(ProjectTypeConstants.MILESTONE,
                UserUIContext.getMessage(MilestoneI18nEnum.LIST), SimpleMilestone::class.java,
                AppContextUtil.getSpringBean(MilestoneService::class.java), variableInjector) {

    override fun getDefaultColumns(): Set<TableViewField> =
            newHashSet(milestoneName, startDate, endDate, status, assignee)

    override fun getAvailableColumns(): Set<TableViewField> =
            newHashSet(milestoneName, startDate, endDate, status, assignee, billableHours, nonBillableHours)

    override fun getSampleMap(): Map<String, String> = mapOf(
            milestoneName.field to "Milestone 1",
            startDate.field to UserUIContext.formatDate(LocalDateTime.now().minusDays(30)),
            endDate.field to UserUIContext.formatDate(LocalDateTime.now().plusDays(7)),
            status.field to UserUIContext.getMessage(MilestoneStatus.InProgress),
            assignee.field to "John Adam",
            billableHours.field to "10",
            nonBillableHours.field to "1"
    )
}
