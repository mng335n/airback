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
import com.airback.db.arguments.SetSearchField
import com.airback.module.project.ProjectMemberStatusConstants
import com.airback.module.project.domain.criteria.ProjectMemberSearchCriteria
import com.airback.module.project.event.ProjectEvent
import com.airback.module.project.service.ProjectMemberService
import com.airback.module.project.view.ProjectUrlResolver
import com.airback.module.project.view.parameters.ProjectMemberScreenData
import com.airback.module.project.view.parameters.ProjectScreenData
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI
import com.airback.vaadin.EventBusFactory
import com.airback.vaadin.mvp.PageActionChain

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class UserUrlResolver : ProjectUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
        this.addSubResolver("preview", PreviewUrlResolver())
        this.addSubResolver("add", AddUrlResolver())
        this.addSubResolver("edit", EditUrlResolver())
    }

    private class ListUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val projectId = UrlTokenizer(params[0]).getInt()
            val memberSearchCriteria = ProjectMemberSearchCriteria()
            memberSearchCriteria.projectIds = SetSearchField(projectId)
            memberSearchCriteria.statuses = SetSearchField(ProjectMemberStatusConstants.ACTIVE, ProjectMemberStatusConstants.NOT_ACCESS_YET)
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), ProjectMemberScreenData.Search(memberSearchCriteria))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

    private class PreviewUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val projectId = token.getInt()
            val memberName = token.getString()
            val chain = PageActionChain(ProjectScreenData.Goto(projectId), ProjectMemberScreenData.Read(memberName))
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

    private class AddUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val projectId = token.getInt()
            val chain = PageActionChain(ProjectScreenData.Goto(projectId),
                    ProjectMemberScreenData.InviteProjectMembers())
            EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
        }
    }

    private class EditUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val projectId = token.getInt()
            val memberId = token.getInt()
            val projectMemberService = AppContextUtil.getSpringBean(ProjectMemberService::class.java)
            val member = projectMemberService.findById(memberId, AppUI.accountId)
            if (member != null) {
                val chain = PageActionChain(ProjectScreenData.Goto(projectId), ProjectMemberScreenData.Add(member))
                EventBusFactory.getInstance().post(ProjectEvent.GotoMyProject(this, chain))
            } else {
                throw ResourceNotFoundException("Can not find member $memberId")
            }
        }
    }
}