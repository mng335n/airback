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
package com.airback.module.project.view.settings

import com.airback.common.UrlTokenizer
import com.airback.core.ResourceNotFoundException
import com.airback.db.arguments.NumberSearchField
import com.airback.module.project.event.ProjectEvent
import com.airback.module.project.view.ProjectUrlResolver
import com.airback.module.project.view.parameters.ProjectScreenData
import com.airback.module.project.view.parameters.VersionScreenData
import com.airback.module.project.domain.Version
import com.airback.module.project.domain.criteria.VersionSearchCriteria
import com.airback.module.project.service.VersionService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI
import com.airback.vaadin.EventBusFactory
import com.airback.vaadin.mvp.PageActionChain

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class VersionUrlResolver : ProjectUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
        this.addSubResolver("add", AddUrlResolver())
        this.addSubResolver("edit", EditUrlResolver())
        this.addSubResolver("preview", PreviewUrlResolver())
    }

    private class ListUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val projectId = UrlTokenizer(params[0]).getInt()
            val versionSearchCriteria = VersionSearchCriteria()
            versionSearchCriteria.projectId = NumberSearchField(projectId)
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), VersionScreenData.Search(versionSearchCriteria))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

    private class PreviewUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val projectId = token.getInt()
            val versionId = token.getInt()
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), VersionScreenData.Read(versionId))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

    private class EditUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val projectId = token.getInt()
            val versionId = token.getInt()
            val versionService = AppContextUtil.getSpringBean(VersionService::class.java)
            val version = versionService.findById(versionId, AppUI.accountId)
            if (version != null) {
                val chain = PageActionChain(ProjectScreenData.Goto(projectId), VersionScreenData.Edit(version))
                EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
            } else {
                throw ResourceNotFoundException("Can not find version $params")
            }
        }
    }

    private class AddUrlResolver : ProjectUrlResolver() {
        protected override fun handlePage(vararg params: String) {
            val projectId = UrlTokenizer(params[0]).getInt()
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), VersionScreenData.Add(Version()))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }
}