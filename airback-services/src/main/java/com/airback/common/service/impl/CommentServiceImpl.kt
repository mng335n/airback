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
package com.airback.common.service.impl

import com.google.common.eventbus.AsyncEventBus
import com.airback.cache.CleanCacheEvent
import com.airback.common.ActivityStreamConstants
import com.airback.common.ModuleNameConstants
import com.airback.common.MonitorTypeConstants
import com.airback.common.dao.CommentMapper
import com.airback.common.dao.CommentMapperExt
import com.airback.common.domain.ActivityStreamWithBLOBs
import com.airback.common.domain.CommentWithBLOBs
import com.airback.common.domain.RelayEmailNotificationWithBLOBs
import com.airback.common.domain.criteria.CommentSearchCriteria
import com.airback.common.service.ActivityStreamService
import com.airback.common.service.CommentService
import com.airback.common.service.RelayEmailNotificationService
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.service.MessageService
import com.airback.module.project.service.TaskService
import com.airback.module.project.service.ProjectTicketService
import com.airback.module.project.service.RiskService
import com.airback.module.project.service.BugService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class CommentServiceImpl(private val commentMapper: CommentMapper,
                         private val commentMapperExt: CommentMapperExt,
                         private val relayEmailNotificationService: RelayEmailNotificationService,
                         private val activityStreamService: ActivityStreamService,
                         private val asyncEventBus: AsyncEventBus) : DefaultService<Int, CommentWithBLOBs, CommentSearchCriteria>(), CommentService {

    override val crudMapper: ICrudGenericDAO<Int, CommentWithBLOBs>
        get() = commentMapper as ICrudGenericDAO<Int, CommentWithBLOBs>

    override val searchMapper: ISearchableDAO<CommentSearchCriteria>
        get() = commentMapperExt

    override fun saveWithSession(record: CommentWithBLOBs, username: String?): Int {
        val saveId = super.saveWithSession(record, username)

        when {
            ProjectTypeConstants.MESSAGE == record.type -> asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf<Class<*>>(MessageService::class.java)))
            ProjectTypeConstants.RISK == record.type -> asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf<Class<*>>(RiskService::class.java, ProjectTicketService::class.java)))
            ProjectTypeConstants.TASK == record.type -> asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf<Class<*>>(TaskService::class.java, ProjectTicketService::class.java)))
            ProjectTypeConstants.BUG == record.type -> asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf<Class<*>>(BugService::class.java, ProjectTicketService::class.java)))
        }

        relayEmailNotificationService.saveWithSession(getRelayEmailNotification(record), username)
        activityStreamService.saveWithSession(getActivityStream(record, username), username)
        return saveId
    }

    private fun getActivityStream(record: CommentWithBLOBs, username: String?): ActivityStreamWithBLOBs {
        val activityStream = ActivityStreamWithBLOBs()
        activityStream.action = ActivityStreamConstants.ACTION_COMMENT
        activityStream.createduser = username
        activityStream.saccountid = record.saccountid
        activityStream.type = record.type
        activityStream.typeid = record.typeid
        activityStream.namefield = record.comment
        activityStream.extratypeid = record.extratypeid
        when {
            record.type != null && record.type.startsWith("Project-") -> activityStream.module = ModuleNameConstants.PRJ
            record.type != null && record.type.startsWith("Crm-") -> activityStream.module = ModuleNameConstants.CRM
            else -> LOG.error("Can not define module type of bean $record")
        }
        return activityStream
    }

    private fun getRelayEmailNotification(record: CommentWithBLOBs): RelayEmailNotificationWithBLOBs {
        val relayEmailNotification = RelayEmailNotificationWithBLOBs()
        relayEmailNotification.saccountid = record.saccountid
        relayEmailNotification.action = MonitorTypeConstants.ADD_COMMENT_ACTION
        relayEmailNotification.changeby = record.createduser
        relayEmailNotification.changecomment = record.comment
        relayEmailNotification.type = record.type
        relayEmailNotification.typeid = record.typeid
        relayEmailNotification.extratypeid = record.extratypeid
        return relayEmailNotification
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CommentServiceImpl::class.java)
    }
}