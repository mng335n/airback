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
import org.springframework.stereotype.Component

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Component
class DeleteProjectMilestoneCommand(private val resourceService: ResourceService,
                                    private val commentMapper: CommentMapper,
                                    private val tagService: TagService) : GenericCommand() {

    @AllowConcurrentEvents
    @Subscribe
    fun removedMilestone(event: DeleteProjectMilestoneEvent) {
        removeRelatedFiles(event.accountId, event.projectId, event.milestoneId)
        removeRelatedComments(event.milestoneId)
        removeRelatedTags(event.milestoneId)
    }

    private fun removeRelatedFiles(accountId: Int, projectId: Int, milestoneId: Int) {
        val attachmentPath = AttachmentUtils.getProjectEntityAttachmentPath(accountId, projectId,
                ProjectTypeConstants.MILESTONE, "" + milestoneId)
        resourceService.removeResource(attachmentPath, "", true, accountId)
    }

    private fun removeRelatedComments(milestoneId: Int) {
        val ex = CommentExample()
        ex.createCriteria().andTypeEqualTo(ProjectTypeConstants.MILESTONE).andExtratypeidEqualTo(milestoneId)
        commentMapper.deleteByExample(ex)
    }

    private fun removeRelatedTags(milestoneId: Int) {
        val ex = TagExample()
        ex.createCriteria().andTypeEqualTo(ProjectTypeConstants.MILESTONE).andTypeidEqualTo( "$milestoneId")
        tagService.deleteByExample(ex)
    }
}