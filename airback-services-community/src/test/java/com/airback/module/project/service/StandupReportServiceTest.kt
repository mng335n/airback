package com.airback.module.project.service

import com.airback.db.arguments.*
import com.airback.module.project.domain.SimpleStandupReport
import com.airback.module.project.domain.criteria.StandupReportSearchCriteria
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class, DbUnitInitializerRule::class)
class StandupReportServiceTest : IntegrationServiceTest() {
    @Autowired
    private lateinit var reportService: StandupReportService

    @Test
    @DataSet
    fun gatherStandupList() {
        val criteria = StandupReportSearchCriteria()
        criteria.projectIds = SetSearchField(1)
        criteria.logBy = StringSearchField.and("anhminh")
        val d = LocalDate.of(2013, 3, 13);
        criteria.onDate = DateSearchField(d, DateSearchField.EQUAL)
        criteria.saccountid = NumberSearchField(1)
        val reports = reportService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleStandupReport>
        assertThat(reports.size).isEqualTo(1)
        assertThat(reports).extracting("id", "logby", "whattoday").contains(tuple(1, "anhminh", "a"))
    }

    @Test
    @DataSet
    fun testFindUsersNotDoReportYet() {
        val d = LocalDate.of(2013, 3, 13)
        val users = reportService.findUsersNotDoReportYet(1, d, 1)
        assertThat(users.size).isEqualTo(1)
        assertThat(users[0].username).isEqualTo("linhduong")
    }
}
