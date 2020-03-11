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
package com.airback.module.project.ui.format

import com.airback.common.i18n.GenericI18Enum
import com.airback.module.project.domain.Milestone
import com.airback.module.project.i18n.OptionI18nEnum
import com.airback.vaadin.ui.formatter.FieldGroupFormatter
import com.airback.vaadin.ui.formatter.I18nHistoryFieldFormat

/**
 * @author airback Ltd
 * @since 5.1.4
 */
class MilestoneFieldFormatter private constructor() : FieldGroupFormatter() {

    init {
        generateFieldDisplayHandler("name", GenericI18Enum.FORM_NAME)
        generateFieldDisplayHandler("status", GenericI18Enum.FORM_STATUS,
                I18nHistoryFieldFormat(OptionI18nEnum.MilestoneStatus::class.java))
        generateFieldDisplayHandler(Milestone.Field.assignuser.name, GenericI18Enum.FORM_ASSIGNEE,
                ProjectMemberHistoryFieldFormat())
        generateFieldDisplayHandler("startdate", GenericI18Enum.FORM_START_DATE, FieldGroupFormatter.DATE_FIELD)
        generateFieldDisplayHandler("enddate", GenericI18Enum.FORM_END_DATE, FieldGroupFormatter.DATE_FIELD)
        generateFieldDisplayHandler(Milestone.Field.description.name, GenericI18Enum.FORM_DESCRIPTION, FieldGroupFormatter.TRIM_HTMLS)
    }

    companion object {
        private val _instance = MilestoneFieldFormatter()

        fun instance(): MilestoneFieldFormatter = _instance
    }
}
