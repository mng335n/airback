package com.airback.module.project.service.impl

import com.google.common.eventbus.AsyncEventBus
import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.aspect.Traceable
import com.airback.cache.CleanCacheEvent
import com.airback.common.ModuleNameConstants
import com.airback.core.cache.CacheKey
import com.airback.db.arguments.DateSearchField
import com.airback.db.arguments.SearchRequest
import com.airback.db.arguments.SetSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.StandupReportMapper
import com.airback.module.project.dao.StandupReportMapperExt
import com.airback.module.project.domain.SimpleStandupReport
import com.airback.module.project.domain.StandupReportStatistic
import com.airback.module.project.domain.StandupReportWithBLOBs
import com.airback.module.project.domain.criteria.StandupReportSearchCriteria
import com.airback.module.project.service.ProjectActivityStreamService
import com.airback.module.project.service.StandupReportService
import com.airback.module.user.domain.SimpleUser
import org.apache.commons.collections.CollectionUtils
import org.apache.ibatis.session.RowBounds
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
@Transactional
@Traceable(nameField = "forday", extraFieldName = "projectid")
class StandupReportServiceImpl(private val standupReportMapper: StandupReportMapper,
                               private val standupReportMapperExt: StandupReportMapperExt,
                               private val asyncEventBus: AsyncEventBus) : DefaultService<Int, StandupReportWithBLOBs, StandupReportSearchCriteria>(), StandupReportService {

    override val crudMapper: ICrudGenericDAO<Int, StandupReportWithBLOBs>
        get() = standupReportMapper as ICrudGenericDAO<Int, StandupReportWithBLOBs>

    override val searchMapper: ISearchableDAO<StandupReportSearchCriteria>
        get() = standupReportMapperExt

    override fun findById(standupId: Int, sAccountId: Int): SimpleStandupReport? =
            standupReportMapperExt.findReportById(standupId)

    override fun findStandupReportByDateUser(projectId: Int, username: String, onDate: LocalDate, sAccountId: Int): SimpleStandupReport? {
        val criteria = StandupReportSearchCriteria()
        criteria.projectIds = SetSearchField(projectId)
        criteria.logBy = StringSearchField.and(username)
        criteria.onDate = DateSearchField(onDate, DateSearchField.EQUAL)
        val reports = standupReportMapperExt.findPageableListByCriteria(criteria, RowBounds(0, Integer.MAX_VALUE))

        return if (CollectionUtils.isNotEmpty(reports)) {
            reports[0] as SimpleStandupReport
        } else null

    }

    override fun saveWithSession(record: StandupReportWithBLOBs, username: String?): Int {
        val result = super.saveWithSession(record, username)
        asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf(ProjectActivityStreamService::class.java)))
        return result
    }

    override fun updateWithSession(record: StandupReportWithBLOBs, username: String?): Int {
        asyncEventBus.post(CleanCacheEvent(record.saccountid, arrayOf(ProjectActivityStreamService::class.java)))
        return super.updateWithSession(record, username)
    }

    override fun massRemoveWithSession(items: List<StandupReportWithBLOBs>, username: String?, sAccountId: Int) {
        super.massRemoveWithSession(items, username, sAccountId)
        asyncEventBus.post(CleanCacheEvent(sAccountId, arrayOf(ProjectActivityStreamService::class.java)))
    }

    override fun findUsersNotDoReportYet(projectId: Int, onDate: LocalDate, @CacheKey sAccountId: Int): List<SimpleUser> =
            standupReportMapperExt.findUsersNotDoReportYet(projectId, onDate)

    override fun getProjectReportsStatistic(projectIds: List<Int>, onDate: LocalDate, searchRequest: SearchRequest): List<StandupReportStatistic> =
            standupReportMapperExt.getProjectReportsStatistic(projectIds, onDate,
                    RowBounds((searchRequest.currentPage - 1) * searchRequest.numberOfItems,
                            searchRequest.numberOfItems))

    companion object {
        init {
            ClassInfoMap.put(StandupReportServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.STANDUP))
        }
    }
}