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
import com.airback.common.domain.GroupItem
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum
import com.airback.concurrent.DistributionLockUtil
import com.airback.core.airbackException
import com.airback.core.cache.CacheKey
import com.airback.core.cache.CleanCache
import com.airback.core.utils.StringUtils
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.SearchCriteria
import com.airback.db.arguments.SearchField
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.TaskMapper
import com.airback.module.project.dao.TaskMapperExt
import com.airback.module.project.domain.SimpleTask
import com.airback.module.project.domain.Task
import com.airback.module.project.domain.TaskExample
import com.airback.module.project.domain.criteria.TaskSearchCriteria
import com.airback.module.project.esb.DeleteProjectTaskEvent
import com.airback.module.project.i18n.OptionI18nEnum.Priority
import com.airback.module.project.service.*
import org.apache.ibatis.session.RowBounds
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
@Transactional
@Traceable(nameField = "name", extraFieldName = "projectid")
class TaskServiceImpl(private val taskMapper: TaskMapper,
                      private val taskMapperExt: TaskMapperExt,
                      private val ticketKeyService: TicketKeyService,
                      private val asyncEventBus: AsyncEventBus,
                      private val dataSource: DataSource) : DefaultService<Int, Task, TaskSearchCriteria>(), TaskService {
    override val crudMapper: ICrudGenericDAO<Int, Task>
        get() = taskMapper as ICrudGenericDAO<Int, Task>

    override val searchMapper: ISearchableDAO<TaskSearchCriteria>
        get() = taskMapperExt

    override fun findById(taskId: Int, sAccountId: Int) = taskMapperExt.findTaskById(taskId)

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    override fun saveWithSession(record: Task, username: String?): Int {
        if (record.percentagecomplete == null) {
            record.percentagecomplete = 0.0
        }

        if (StringUtils.isBlank(record.status)) {
            record.status = StatusI18nEnum.Open.name
        }

        if (record.priority == null) {
            record.priority = Priority.Medium.name
        }
        record.createduser = username
        val lock = DistributionLockUtil.getLock("task-${record.saccountid!!}")

        try {
            if (lock.tryLock(120, TimeUnit.SECONDS)) {
                val key = ticketKeyService.getMaxKey(record.projectid!!)
                val taskKey = if (key == null) 1 else key + 1

                val taskId =  super.saveWithSession(record, username)
                ticketKeyService.saveKey(record.projectid!!, taskId, ProjectTypeConstants.TASK, taskKey)
                return taskId
            } else {
                throw airbackException("Timeout operation.")
            }
        } catch (e: InterruptedException) {
            throw airbackException(e)
        } finally {
            DistributionLockUtil.removeLock("task-${record.saccountid!!}")
            lock.unlock()
        }
    }

    @Transactional
    override fun updateWithSession(record: Task, username: String?): Int {
        beforeUpdate(record)
        return super.updateWithSession(record, username)
    }

    private fun beforeUpdate(record: Task) {
        if (record.status == null) {
            record.status = StatusI18nEnum.Open.name
        } else if (StatusI18nEnum.Closed.name == record.status) {
            record.percentagecomplete = 100.0
        }
    }

    override fun updateSelectiveWithSession(record: Task, username: String?): Int? {
        beforeUpdate(record)
        return super.updateSelectiveWithSession(record, username)!!
    }

    @CleanCache
    fun postDirtyUpdate(sAccountId: Int?) {
        asyncEventBus.post(CleanCacheEvent(sAccountId, arrayOf(ProjectService::class.java,
                ProjectTicketService::class.java, ProjectActivityStreamService::class.java,
                ProjectMemberService::class.java, MilestoneService::class.java, ItemTimeLoggingService::class.java)))
    }

    override fun removeWithSession(item: Task, username: String?, sAccountId: Int) {
        super.removeWithSession(item, username, sAccountId)
        val event = DeleteProjectTaskEvent(arrayOf(item), username, sAccountId)
        asyncEventBus.post(event)
    }

    override fun massRemoveWithSession(items: List<Task>, username: String?, sAccountId: Int) {
        super.massRemoveWithSession(items, username, sAccountId)
        val event = DeleteProjectTaskEvent(items.toTypedArray(), username, sAccountId)
        asyncEventBus.post(event)
    }

    override fun getPrioritySummary(criteria: TaskSearchCriteria): List<GroupItem> =
            taskMapperExt.getPrioritySummary(criteria)

    override fun getStatusSummary(@CacheKey criteria: TaskSearchCriteria): List<GroupItem> =
            taskMapperExt.getStatusSummary(criteria)

    override fun getAssignedTasksSummary(criteria: TaskSearchCriteria): List<GroupItem> =
            taskMapperExt.getAssignedDefectsSummary(criteria)

    override fun findSubTasks(parentTaskId: Int, sAccountId: Int, orderField: SearchCriteria.OrderField): List<SimpleTask> {
        val searchCriteria = TaskSearchCriteria()
        searchCriteria.saccountid = NumberSearchField(sAccountId)
        searchCriteria.parentTaskId = NumberSearchField(parentTaskId)
        searchCriteria.setOrderFields(arrayListOf(orderField))
        return taskMapperExt.findPageableListByCriteria(searchCriteria, RowBounds(0, Integer.MAX_VALUE)) as List<SimpleTask>
    }

    override fun getCountOfOpenSubTasks(taskId: Int): Int {
        val searchCriteria = TaskSearchCriteria()
        searchCriteria.parentTaskId = NumberSearchField(taskId)
        searchCriteria.addExtraField(TaskSearchCriteria.p_status.buildPropertyParamNotInList(SearchField.AND,
                setOf(StatusI18nEnum.Closed.name)))
        return taskMapperExt.getTotalCount(searchCriteria)
    }

    override fun massUpdateTaskStatuses(parentTaskId: Int, status: String, @CacheKey sAccountId: Int) {
        val searchCriteria = TaskSearchCriteria()
        searchCriteria.parentTaskId = NumberSearchField(parentTaskId)
        searchCriteria.saccountid = NumberSearchField(sAccountId)
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.update("UPDATE `m_prj_task` SET `status`=? WHERE `parentTaskId`=?", status, parentTaskId)
    }

    override fun massUpdateStatuses(oldStatus: String, newStatus: String, projectId: Int, @CacheKey sAccountId: Int) {
        val updateTaskStatus = Task()
        updateTaskStatus.status = newStatus
        val ex = TaskExample()
        ex.createCriteria().andStatusEqualTo(oldStatus).andProjectidEqualTo(projectId).andSaccountidEqualTo(sAccountId)
        taskMapper.updateByExampleSelective(updateTaskStatus, ex)
    }

    companion object {
        init {
            val taskInfo = ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.TASK)
            ClassInfoMap.put(TaskServiceImpl::class.java, taskInfo)
        }
    }
}
