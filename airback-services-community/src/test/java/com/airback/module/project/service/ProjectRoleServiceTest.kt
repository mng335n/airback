package com.airback.module.project.service

import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@ExtendWith(DbUnitInitializerRule::class)
class ProjectRoleServiceTest(@Autowired val projectRoleService: ProjectRoleService) : IntegrationServiceTest() {

    @Test
    @DataSet
    fun testFindByCriteria() {
        val criteria = ProjectRoleSearchCriteria()
        criteria.projectId = NumberSearchField.equal(1)
        criteria.saccountid = NumberSearchField.equal(1)
        criteria.roleName = StringSearchField.and("role1")
        val roles = projectRoleService.findPageableListByCriteria(BasicSearchRequest(criteria))
        assertThat(roles.size).isEqualTo(1)

        assertThat(projectRoleService.getTotalCount(criteria)).isEqualTo(1)
    }

    @Test
    @DataSet
    fun testFindRole() {
        val role = projectRoleService.findById(1, 1)
        assertThat(role).extracting("rolename").contains("role1")
    }

    @Test
    @DataSet
    fun testFindProjectPermissions() {
        val permissions = projectRoleService.findProjectsPermissions("anhminh@airback.com", Arrays.asList(1), 1)
        assertThat(permissions.size).isEqualTo(1)
        assertThat(permissions[0].item1).isEqualTo(1)
    }
}