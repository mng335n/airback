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
package com.airback.module.project.esb

import com.google.common.eventbus.AllowConcurrentEvents
import com.google.common.eventbus.Subscribe
import com.airback.common.dao.CommentMapper
import com.airback.common.domain.CommentExample
import com.airback.common.domain.TagExample
import com.airback.common.service.TagService
import com.airback.module.ecm.service.ResourceService
import com.airback.module.esb.GenericCommand
import com.airback.module.file.AttachmentUtils
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.TicketKeyMapper
import com.airback.module.project.dao.TicketRelationMapper
import com.airback.module.project.domain.TicketKeyExample
import com.airback.module.project.domain.TicketRelationExample
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
class DeleteProjectBugCommand(private val resourceService: ResourceService,
                              private val commentMapper: CommentMapper,
                              private val tagService: TagService,
                              private val ticketKeyMapper: TicketKeyMapper,
                              private val ticketRelationMapper: TicketRelationMapper) : GenericCommand() {

    @AllowConcurrentEvents
    @Subscribe
    fun removeBugs(event: DeleteProjectBugEvent) {
        val bugIds = event.bugs.map { it.id }.toCollection(mutableListOf())
        event.bugs.forEach {
            removeRelatedFiles(event.accountId, it.projectid, it.id)
            removeRelatedTags(it.id)
        }
        removeRelatedComments(bugIds)
        removeTicketKeys(bugIds)
        removeTicketRelations(bugIds);
    }

    private fun removeRelatedFiles(accountId: Int, projectId: Int, bugId: Int) {
        val attachmentPath = AttachmentUtils.getProjectEntityAttachmentPath(accountId, projectId,
                ProjectTypeConstants.BUG, "$bugId")
        resourceService.removeResource(attachmentPath, "", true, accountId)
    }

    private fun removeRelatedComments(bugIds: MutableList<Int>) {
        val ex = CommentExample()
        ex.createCriteria().andTypeEqualTo(ProjectTypeConstants.BUG).andExtratypeidIn(bugIds)
        commentMapper.deleteByExample(ex)
    }

    private fun removeTicketKeys(bugIds: MutableList<Int>) {
        val ex = TicketKeyExample()
        ex.createCriteria().andTicketidIn(bugIds).andTickettypeEqualTo(ProjectTypeConstants.BUG)
        ticketKeyMapper.deleteByExample(ex)
    }

    private fun removeTicketRelations(bugIds: MutableList<Int>) {
        val ex = TicketRelationExample()
        ex.createCriteria().andTicketidIn(bugIds).andTickettypeEqualTo(ProjectTypeConstants.BUG)
        ticketRelationMapper.deleteByExample(ex)
    }

    private fun removeRelatedTags(bugId: Int) {
        val ex = TagExample()
        ex.createCriteria().andTypeEqualTo(ProjectTypeConstants.BUG).andTypeidEqualTo("$bugId")
        tagService.deleteByExample(ex)
    }
}