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

import com.airback.common.UrlTokenizer
import com.airback.db.arguments.SetSearchField
import com.airback.db.query.DateParam
import com.airback.db.query.VariableInjector
import com.airback.vaadin.EventBusFactory
import com.airback.module.project.domain.criteria.ItemTimeLoggingSearchCriteria
import com.airback.module.project.event.ProjectEvent
import com.airback.module.project.view.parameters.ProjectScreenData
import com.airback.module.project.view.parameters.TimeTrackingScreenData
import com.airback.vaadin.mvp.PageActionChain

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class TimeUrlResolver : ProjectUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
    }

    private class ListUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val projectId = UrlTokenizer(params[0]).getInt()
            val searchCriteria = ItemTimeLoggingSearchCriteria()
            searchCriteria.projectIds = SetSearchField(projectId)
            searchCriteria.addExtraField(DateParam.inRangeDate(ItemTimeLoggingSearchCriteria.p_logDates, VariableInjector.THIS_WEEK))
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), TimeTrackingScreenData.Search(searchCriteria))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

}