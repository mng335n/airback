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
package com.airback.common.domain

import com.airback.core.utils.StringUtils
import com.airback.module.ecm.domain.Content
import com.airback.module.ecm.service.ContentJcrDao
import com.airback.module.file.AttachmentUtils
import com.airback.spring.AppContextUtil
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author airback Ltd.
 * @since 1.0
 */
class SimpleComment : CommentWithBLOBs() {

    var ownerAvatarId: String? = null
    var ownerFullName: String? = null
        get() {
            if (StringUtils.isBlank(field)) {
                val displayName = createduser
                return StringUtils.extractNameFromEmail(displayName)
            }
            return field
        }
    private var attachments: List<Content>? = null

    fun getAttachments(): List<Content> {
        try {
            if (attachments == null) {
                val contentJcr = AppContextUtil.getSpringBean(ContentJcrDao::class.java)
                val commentPath = AttachmentUtils.getCommentAttachmentPath(type, saccountid, extratypeid, typeid, id!!)
                attachments = contentJcr.getContents(commentPath)
            }
        } catch (e: Exception) {
            LOG.error("Error while get attachments of comment $id---$saccountid---$extratypeid---$typeid", e)
        }

        if (attachments == null) {
            attachments = ArrayList()
        }
        return attachments!!
    }

    companion object {
        private val serialVersionUID = 1L
        private val LOG = LoggerFactory.getLogger(SimpleComment::class.java)
    }
}
