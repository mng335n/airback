package com.airback.module.project.service

import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.SetSearchField
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.domain.criteria.ProjectGenericItemSearchCriteria
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(DbUnitInitializerRule::class)
class GenericTicketServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var projectGenericItemService: ProjectGenericItemService

    @DataSet
    @Test
    fun testFindItems() {
        val criteria = ProjectGenericItemSearchCriteria()
        criteria.prjKeys = SetSearchField(1)
        criteria.types = SetSearchField(ProjectTypeConstants.BUG, ProjectTypeConstants.TASK, ProjectTypeConstants.RISK,
                ProjectTypeConstants.MESSAGE)
        val items = projectGenericItemService.findPageableListByCriteria(BasicSearchRequest(criteria))
        assertThat(items.size).isEqualTo(6)
        assertThat(items).extracting("name", "projectName").contains(tuple("Risk 1", "a"))
    }

    @DataSet
    @Test
    fun testGetCount() {
        val criteria = ProjectGenericItemSearchCriteria()
        criteria.prjKeys = SetSearchField(1)
        criteria.types = SetSearchField(ProjectTypeConstants.BUG, ProjectTypeConstants.TASK, ProjectTypeConstants.RISK,
                ProjectTypeConstants.MESSAGE)
        val totalCount = projectGenericItemService.getTotalCount(criteria)
        assertThat(totalCount).isEqualTo(6)
    }
}