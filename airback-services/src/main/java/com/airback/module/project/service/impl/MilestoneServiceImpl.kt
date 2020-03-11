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
import com.airback.core.cache.CacheKey
import com.airback.core.cache.CleanCache
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.MilestoneMapper
import com.airback.module.project.dao.MilestoneMapperExt
import com.airback.module.project.domain.Milestone
import com.airback.module.project.domain.SimpleMilestone
import com.airback.module.project.domain.criteria.MilestoneSearchCriteria
import com.airback.module.project.i18n.OptionI18nEnum.MilestoneStatus
import com.airback.module.project.service.*
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.DataSource

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
@Transactional
@Traceable(nameField = "name", extraFieldName = "projectid")
class MilestoneServiceImpl(private val milestoneMapper: MilestoneMapper,
                           private val milestoneMapperExt: MilestoneMapperExt,
                           private val dataSource: DataSource,
                           private val asyncEventBus: AsyncEventBus) : DefaultService<Int, Milestone, MilestoneSearchCriteria>(), MilestoneService {

    override val crudMapper: ICrudGenericDAO<Int, Milestone>
        get() = milestoneMapper as ICrudGenericDAO<Int, Milestone>

    override val searchMapper: ISearchableDAO<MilestoneSearchCriteria>
        get() = milestoneMapperExt

    override fun findById(milestoneId: Int, sAccountId: Int): SimpleMilestone? =
            milestoneMapperExt.findById(milestoneId)

    override fun saveWithSession(record: Milestone, username: String?): Int {
        if (record.status == null) {
            record.status = MilestoneStatus.InProgress.name
        }
        return super.saveWithSession(record, username)
    }

    @CleanCache
    fun postDirtyUpdate(sAccountId: Int?) {
        asyncEventBus.post(CleanCacheEvent(sAccountId, arrayOf(ProjectService::class.java, ProjectTicketService::class.java, ProjectActivityStreamService::class.java)))
    }

    override fun massUpdateOptionIndexes(mapIndexes: List<Map<String, Int>>, @CacheKey sAccountId: Int) {
        val jdbcTemplate = JdbcTemplate(dataSource)
        jdbcTemplate.batchUpdate("UPDATE `m_prj_milestone` SET `orderIndex`=? WHERE `id`=?", object : BatchPreparedStatementSetter {
            @Throws(SQLException::class)
            override fun setValues(preparedStatement: PreparedStatement, i: Int) {
                preparedStatement.setInt(1, mapIndexes[i]["index"]!!)
                preparedStatement.setInt(2, mapIndexes[i]["id"]!!)
            }

            override fun getBatchSize(): Int = mapIndexes.size
        })
    }

    companion object {
        init {
            ClassInfoMap.put(MilestoneServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.MILESTONE))
        }
    }
}
