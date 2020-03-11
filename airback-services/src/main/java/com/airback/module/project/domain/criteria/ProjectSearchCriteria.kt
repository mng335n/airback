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

import com.airback.common.i18n.GenericI18Enum
import com.airback.common.i18n.OptionI18nEnum
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum.*
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.SearchCriteria
import com.airback.db.arguments.SetSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.module.project.ProjectTypeConstants
import com.airback.db.query.*

import java.util.Arrays

/**
 * @author airback Ltd.
 * @since 1.0
 */
class ProjectSearchCriteria : SearchCriteria() {

    var projectKeys: SetSearchField<Int>? = null
    var statuses: SetSearchField<String>? = null
    var involvedMember: StringSearchField? = null
    var projectName: StringSearchField? = null
    var clientId: NumberSearchField? = null

    companion object {
        private const val serialVersionUID = 1L

        @JvmField
        val p_template = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_NAME,
                NumberParam("template", "m_prj_project", "istemplate"))

        @JvmField
        val p_name = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_NAME,
                StringParam("name", "m_prj_project", "name"))

        @JvmField
        val p_startdate = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_START_DATE,
                DateParam("startdate", "m_prj_project", "planStartDate"))

        @JvmField
        val p_enddate = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_END_DATE,
                DateParam("enddate", "m_prj_project", "planEndDate"))

        @JvmField
        val p_createdtime = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_CREATED_TIME,
                DateParam("createdtime", "m_prj_project", "createdTime"))

        @JvmField
        val p_status = CacheParamMapper.register(ProjectTypeConstants.PROJECT, GenericI18Enum.FORM_STATUS,
                StringListParam("status", "m_prj_project", "status", setOf(Open.name,
                        Closed.name, Archived.name)))
    }
}
