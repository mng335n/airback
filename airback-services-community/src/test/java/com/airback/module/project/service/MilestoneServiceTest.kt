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
import com.airback.db.arguments.SetSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.module.project.dao.MilestoneMapperExt
import com.airback.module.project.domain.SimpleMilestone
import com.airback.module.project.domain.criteria.MilestoneSearchCriteria
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.text.ParseException

@ExtendWith(SpringExtension::class, DbUnitInitializerRule::class)
class MilestoneServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var milestoneService: MilestoneService

    @Autowired
    private lateinit var milestoneMapperExt: MilestoneMapperExt

    private val criteria: MilestoneSearchCriteria
        get() {
            val criteria = MilestoneSearchCriteria()
            criteria.saccountid = NumberSearchField(1)
            criteria.projectIds = SetSearchField(1)
            return criteria
        }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListMilestones() {
        val criteria = MilestoneSearchCriteria()
        criteria.saccountid = NumberSearchField(1)
        criteria.projectIds = SetSearchField(1)
        criteria.statuses = SetSearchField("Open")
        criteria.milestoneName = StringSearchField.and("milestone 1")

        val milestones = milestoneService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleMilestone>

        assertThat(milestones.size).isEqualTo(1)
        assertThat<SimpleMilestone>(milestones).extracting("id", "description", "createdUserFullName", "ownerFullName",
                "numTasks", "numOpenTasks", "numBugs", "numOpenBugs").contains(
                tuple(1, "milestone no1", "Anh Minh", "Anh Minh", 1, 0, 2, 2))
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListMilestonesByCriteria() {
        val milestones = milestoneService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleMilestone>

        assertThat(milestones.size).isEqualTo(4)
        assertThat<SimpleMilestone>(milestones).extracting("id", "description", "createdUserFullName",
                "numTasks", "numOpenTasks", "numBugs", "numOpenBugs").contains(
                tuple(4, "milestone no4", "Dieu Ha", 0, 0, 3, 3),
                tuple(3, "milestone no3", "Anh Le", 0, 0, 1, 1),
                tuple(2, "milestone no2", "Anh Le", 2, 0, 0, 0),
                tuple(1, "milestone no1", "Anh Minh", 1, 0, 2, 2))
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testFindMilestoneById() {
        val milestone = milestoneService.findById(1, 1)
        assertThat(milestone).extracting("createdUserFullName", "numOpenBugs", "totalTaskBillableHours", "totalBugBillableHours", "totalTaskNonBillableHours", "totalBugNonBillableHours").contains("Anh Minh", 2, 2.0, 3.0, 4.0, 5.0)
    }

    @DataSet
    @Test
    fun testGetTotalCount() {
        val milestoneSize = milestoneService.getTotalCount(criteria)
        assertThat(milestoneSize).isEqualTo(4)
    }

    @DataSet
    @Test
    fun testGetTotalBillableHours() {
        assertThat(milestoneMapperExt.getTotalBillableHours(1)).isEqualTo(5.0)
    }

    @DataSet
    @Test
    fun testGetNonTotalBillableHours() {
        assertThat(milestoneMapperExt.getTotalNonBillableHours(1)).isEqualTo(9.0)
    }
}
