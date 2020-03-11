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
package com.airback.module.project.view.settings

import com.google.common.collect.Sets.newHashSet
import com.airback.common.TableViewField
import com.airback.db.query.VariableInjector
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.SimpleProjectMember
import com.airback.module.project.domain.criteria.ProjectMemberSearchCriteria
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.billingRate
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.memberName
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.numOpenBugs
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.numOpenTasks
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.overtimeRate
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.projectName
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.roleName
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.totalBillableLogTime
import com.airback.module.project.fielddef.ProjectMemberTableFieldDef.totalNonBillableLogTime
import com.airback.module.project.i18n.ProjectMemberI18nEnum
import com.airback.module.project.service.ProjectMemberService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.reporting.CustomizeReportOutputWindow

/**
 * @author airback Ltd
 * @since 5.3.4
 */
class ProjectMemberCustomizeReportOutputWindow(variableInjector: VariableInjector<ProjectMemberSearchCriteria>) :
        CustomizeReportOutputWindow<ProjectMemberSearchCriteria, SimpleProjectMember>(ProjectTypeConstants.MEMBER,
                UserUIContext.getMessage(ProjectMemberI18nEnum.LIST), SimpleProjectMember::class.java,
                AppContextUtil.getSpringBean(ProjectMemberService::class.java), variableInjector) {

    override fun getDefaultColumns(): Set<TableViewField> =
            newHashSet(memberName, roleName, billingRate, overtimeRate)

    override fun getAvailableColumns(): Set<TableViewField> =
            newHashSet(projectName, memberName, roleName, numOpenTasks, numOpenBugs, totalBillableLogTime,
                    totalNonBillableLogTime, billingRate, overtimeRate)

    override fun getSampleMap(): Map<String, String> = mapOf(
            projectName.field to "airback",
            memberName.field to "John Adam",
            roleName.field to "Administrator",
            numOpenTasks.field to "12",
            numOpenBugs.field to "3",
            totalBillableLogTime.field to "40",
            totalNonBillableLogTime.field to "8",
            billingRate.field to "50",
            overtimeRate.field to "70"
    )
}
