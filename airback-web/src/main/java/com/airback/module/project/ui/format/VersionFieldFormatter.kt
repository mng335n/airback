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
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum
import com.airback.vaadin.ui.formatter.FieldGroupFormatter
import com.airback.vaadin.ui.formatter.I18nHistoryFieldFormat

/**
 * @author airback Ltd
 * @since 5.1.4
 */
class VersionFieldFormatter private constructor() : FieldGroupFormatter() {

    init {
        generateFieldDisplayHandler("name", GenericI18Enum.FORM_NAME)
        generateFieldDisplayHandler("status", GenericI18Enum.FORM_STATUS, I18nHistoryFieldFormat(StatusI18nEnum::class.java))
        generateFieldDisplayHandler("description", GenericI18Enum.FORM_DESCRIPTION, FieldGroupFormatter.TRIM_HTMLS)
        generateFieldDisplayHandler("duedate", GenericI18Enum.FORM_DUE_DATE, FieldGroupFormatter.DATE_FIELD)
    }

    companion object {
        private val _instance = VersionFieldFormatter()

        fun instance(): VersionFieldFormatter = _instance
    }
}
