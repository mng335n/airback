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
package com.airback.module.user.esb

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.airback.cache.CleanCacheEvent
import com.airback.common.dao.MonitorItemMapper
import com.airback.common.domain.MonitorItemExample
import com.airback.module.esb.GenericCommand
import com.airback.module.project.ProjectMemberStatusConstants
import com.airback.module.project.dao.ProjectMemberMapper
import com.airback.module.project.domain.ProjectMember
import com.airback.module.project.domain.ProjectMemberExample
import com.airback.module.project.service.ProjectMemberService
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
class DeleteUserCommand(private val projectMemberMapper: ProjectMemberMapper,
                        private val monitorItemMapper: MonitorItemMapper) : GenericCommand() {

    @AllowConcurrentEvents
    @Subscribe
    fun execute(event: DeleteUserEvent) {
        removeProjectInvolvement(event)
        removeUserMonitorItems(event)
        asyncEventBus.post(CleanCacheEvent(event.sAccountId, arrayOf(ProjectMemberService::class.java)))
    }

    private fun removeProjectInvolvement(event: DeleteUserEvent) {
        val ex = ProjectMemberExample()
        ex.createCriteria().andStatusNotIn(listOf(ProjectMemberStatusConstants.INACTIVE)).
                andSaccountidEqualTo(event.sAccountId).andUsernameEqualTo(event.username)
        val projectMember = ProjectMember()
        projectMember.status = ProjectMemberStatusConstants.INACTIVE
        projectMemberMapper.updateByExampleSelective(projectMember, ex)
    }

    private fun removeUserMonitorItems(event: DeleteUserEvent) {
        val ex = MonitorItemExample()
        ex.createCriteria().andSaccountidEqualTo(event.sAccountId).andUsernameEqualTo(event.username)
        monitorItemMapper.deleteByExample(ex)
    }
}