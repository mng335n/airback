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
package com.airback.module.project.service.impl

import com.google.common.eventbus.AsyncEventBus
import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.aspect.Traceable
import com.airback.cache.CleanCacheEvent
import com.airback.common.ModuleNameConstants
import com.airback.core.cache.CleanCache
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.MessageMapper
import com.airback.module.project.dao.MessageMapperExt
import com.airback.module.project.domain.Message
import com.airback.module.project.domain.SimpleMessage
import com.airback.module.project.domain.criteria.MessageSearchCriteria
import com.airback.module.project.esb.DeleteProjectMessageEvent
import com.airback.module.project.service.MessageService
import com.airback.module.project.service.ProjectActivityStreamService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
@Transactional
@Traceable(nameField = "title", extraFieldName = "projectid")
class MessageServiceImpl(private val messageMapper: MessageMapper,
                         private val messageMapperExt: MessageMapperExt,
                         private val asyncEventBus: AsyncEventBus) : DefaultService<Int, Message, MessageSearchCriteria>(), MessageService {

    override val crudMapper: ICrudGenericDAO<Int, Message>
        get() = messageMapper as ICrudGenericDAO<Int, Message>

    override val searchMapper: ISearchableDAO<MessageSearchCriteria>
        get() = messageMapperExt

    @CleanCache
    fun postDirtyUpdate(sAccountId: Int?) {
        asyncEventBus.post(CleanCacheEvent(sAccountId, arrayOf(ProjectActivityStreamService::class.java)))
    }

    override fun massRemoveWithSession(items: List<Message>, username: String?, sAccountId: Int) {
        super.massRemoveWithSession(items, username, sAccountId)
        val event = DeleteProjectMessageEvent(items.toTypedArray(), username, sAccountId)
        asyncEventBus.post(event)
    }

    override fun findById(messageId: Int, sAccountId: Int) = messageMapperExt.findMessageById(messageId)

    companion object {

        init {
            ClassInfoMap.put(MessageServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.MESSAGE))
        }
    }
}
