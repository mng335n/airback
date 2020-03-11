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
package com.airback.module.project.view

import com.airback.core.utils.StringUtils
import com.airback.db.arguments.DateSearchField
import com.airback.module.project.domain.criteria.StandupReportSearchCriteria
import com.airback.module.project.event.StandUpEvent
import com.airback.vaadin.EventBusFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class StandupUrlResolver : ProjectUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
    }

    private class ListUrlResolver : ProjectUrlResolver() {
        private val dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")

        override fun handlePage(vararg params: String) {
            val searchCriteria = StandupReportSearchCriteria()
            if (params.isEmpty()) {
                searchCriteria.onDate = DateSearchField(LocalDate.now(), DateSearchField.EQUAL)
            } else {
                val date = parseDate(params[0])
                searchCriteria.onDate = DateSearchField(date, DateSearchField.EQUAL)
            }

            EventBusFactory.getInstance().post(StandUpEvent.GotoList(this, searchCriteria))
        }

        /**
         * @param dateVal
         * @return
         */
        private fun parseDate(dateVal: String?): LocalDate = when {
            StringUtils.isBlank(dateVal) -> LocalDate.now()
            else -> LocalDate.parse(dateVal, dateFormatter)
        }
    }
}