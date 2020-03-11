package com.airback.module.project.dao

import com.airback.db.persistence.ISearchableDAO
import com.airback.module.project.domain.SimpleStandupReport
import com.airback.module.project.domain.StandupReportStatistic
import com.airback.module.project.domain.criteria.StandupReportSearchCriteria
import com.airback.module.user.domain.SimpleUser
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.session.RowBounds
import java.time.LocalDate

/**
 * @author airback Ltd
 * @since 1.0.0
 */
interface StandupReportMapperExt : ISearchableDAO<StandupReportSearchCriteria> {

    fun findReportById(standupId: Int?): SimpleStandupReport?

    fun getProjectReportsStatistic(@Param("projectIds") projectIds: List<Int>, @Param("onDate") onDate: LocalDate,
                                   rowBounds: RowBounds): List<StandupReportStatistic>

    fun findUsersNotDoReportYet(@Param("projectId") projectId: Int?, @Param("onDate") onDate: LocalDate): List<SimpleUser>
}
