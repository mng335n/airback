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
package com.airback.module.project.service

import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.module.project.dao.ComponentMapperExt
import com.airback.module.project.domain.SimpleComponent
import com.airback.module.project.domain.criteria.ComponentSearchCriteria
import com.airback.module.project.service.ComponentService
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import java.text.ParseException

@ExtendWith(DbUnitInitializerRule::class)
class ComponentServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var componentService: ComponentService

    @Autowired
    private lateinit var componentMapperExt: ComponentMapperExt

    private val criteria: ComponentSearchCriteria
        get() {
            val criteria = ComponentSearchCriteria()
            criteria.projectId = NumberSearchField(1)
            criteria.saccountid = NumberSearchField(1)
            return criteria
        }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListComponents() {
        val components = componentService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleComponent>

        assertThat(components.size).isEqualTo(4)
        assertThat<SimpleComponent>(components).extracting("id", "description", "status",
                "name", "numBugs", "numOpenBugs", "userLeadFullName").contains(
                tuple(1, "aaaaaaa", "Open", "com 1", 1, 1, "Anh Minh"),
                tuple(2, "bbbbbbb", "Closed", "com 2", 2, 1, "Anh Le"),
                tuple(3, "ccccccc", "Closed", "com 3", 1, 1, "Anh Minh"),
                tuple(4, "ddddddd", "Open", "com 4", 0, 0, "Anh Le"))
    }

    @DataSet
    @Test
    fun testTotalCount() {
        val components = componentService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleComponent>
        assertThat(components.size).isEqualTo(4)
    }

    @DataSet
    @Test
    fun testFindComponentById() {
        val criteria = ComponentSearchCriteria()
        criteria.id = NumberSearchField(1)

        val components = componentService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleComponent>
        assertThat(components.size).isEqualTo(1)
        assertThat<SimpleComponent>(components).extracting("id", "description", "status",
                "name", "numBugs", "numOpenBugs").contains(
                tuple(1, "aaaaaaa", "Open", "com 1", 1, 1))
    }

    @DataSet
    @Test
    fun testFindByCriteria() {
        val criteria = criteria
        criteria.id = NumberSearchField(2)
        criteria.componentName = StringSearchField.and("com 2")
        criteria.status = StringSearchField.and("Closed")
        criteria.userlead = StringSearchField.and("nghiemle")

        val components = componentService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleComponent>
        assertThat(components.size).isEqualTo(1)
        assertThat<SimpleComponent>(components).extracting("id", "description", "status",
                "name", "numBugs", "numOpenBugs").contains(
                tuple(2, "bbbbbbb", "Closed", "com 2", 2, 1))
    }

    @DataSet
    @Test
    fun testGetTotalBillableHours() {
        assertThat(componentMapperExt.getTotalBillableHours(1)).isEqualTo(3.0)
    }

    @DataSet
    @Test
    fun testGetTotalNonBillableHours() {
        assertThat(componentMapperExt.getTotalNonBillableHours(1)).isEqualTo(7.0)
    }

    @DataSet
    @Test
    fun testGetRemainEstimate() {
        assertThat(componentMapperExt.getRemainHours(1)).isEqualTo(5.0)
    }
}
