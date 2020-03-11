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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.module.project.domain.criteria

import com.airback.db.arguments.*
import com.airback.db.query.BooleanParam
import com.airback.db.query.CacheParamMapper
import com.airback.db.query.DateParam
import com.airback.db.query.PropertyListParam
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.i18n.TimeTrackingI18nEnum
import com.airback.module.user.accountsettings.localization.UserI18nEnum

/**
 * @author airback Ltd.
 * @since 1.0
 */
class ItemTimeLoggingSearchCriteria : SearchCriteria() {

    var projectIds: SetSearchField<Int>? = null
    var logUsers: SetSearchField<String>? = null
    var type: StringSearchField? = null
    var typeId: NumberSearchField? = null
    var isBillable: BooleanSearchField? = null

    companion object {

        @JvmField
        val p_logDates = CacheParamMapper.register(ProjectTypeConstants.TIME,
                TimeTrackingI18nEnum.LOG_FOR_DATE, DateParam("logdate", "m_prj_time_logging", "logForDay"))

        @JvmField
        val p_logUsers = CacheParamMapper.register(ProjectTypeConstants.TIME,
                UserI18nEnum.LIST, PropertyListParam<String>("loguser", "m_prj_time_logging", "loguser"))

        @JvmField
        val p_isBillable = CacheParamMapper.register(ProjectTypeConstants.TIME,
                TimeTrackingI18nEnum.FORM_IS_BILLABLE, BooleanParam("isBillable", "m_prj_time_logging", "isBillable"))

        @JvmField
        val p_isOvertime = CacheParamMapper.register(ProjectTypeConstants.TIME,
                TimeTrackingI18nEnum.FORM_IS_OVERTIME, BooleanParam("isOvertime", "m_prj_time_logging", "isOvertime"))

        @JvmField
        val p_isApproved = CacheParamMapper.register(ProjectTypeConstants.TIME,
                TimeTrackingI18nEnum.FORM_IS_APPROVED, BooleanParam("isApproved", "m_prj_time_logging", "isApproved"))
    }
}
