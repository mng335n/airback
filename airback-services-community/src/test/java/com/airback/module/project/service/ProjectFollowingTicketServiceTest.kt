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
import com.airback.module.project.domain.FollowingTicket
import com.airback.module.project.domain.criteria.FollowingTicketSearchCriteria
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
class ProjectFollowingTicketServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var projectFollowingTicketService: ProjectFollowingTicketService

    private val criteria: FollowingTicketSearchCriteria
        get() {
            val criteria = FollowingTicketSearchCriteria()
            criteria.extraTypeIds = SetSearchField(1, 2)
            criteria.saccountid = NumberSearchField(1)
            criteria.user = StringSearchField.and("anhminh@airback.com")
            return criteria
        }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicket() {
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>
        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Task", "task 1"),
                tuple("Project-Task", "task 2"),
                tuple("Project-Bug", "bug 1"),
                tuple("Project-Bug", "bug 2"),
                tuple("Project-Task", "task 3"),
                tuple("Project-Task", "task 4"),
                tuple("Project-Bug", "bug 3"),
                tuple("Project-Bug", "bug 4"),
                tuple("Project-Risk", "risk 1"),
                tuple("Project-Risk", "risk 2"),
                tuple("Project-Risk", "risk 3"),
                tuple("Project-Risk", "risk 4"))
        assertThat(projectFollowingTickets.size).isEqualTo(12)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicketBySummary() {
        val criteria = criteria
        criteria.name = StringSearchField.and("1")
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>
        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Task", "task 1"),
                tuple("Project-Bug", "bug 1"),
                tuple("Project-Risk", "risk 1"))
        assertThat(projectFollowingTickets.size).isEqualTo(3)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicketOfTaskAndBug() {
        val criteria = criteria
        criteria.types = SetSearchField("Project-Task", "Project-Bug")
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>
        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Task", "task 1"),
                tuple("Project-Task", "task 2"),
                tuple("Project-Task", "task 3"),
                tuple("Project-Task", "task 4"),
                tuple("Project-Bug", "bug 1"),
                tuple("Project-Bug", "bug 2"),
                tuple("Project-Bug", "bug 3"),
                tuple("Project-Bug", "bug 4"))
        assertThat(projectFollowingTickets.size).isEqualTo(8)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicketOfTask() {
        val criteria = criteria
        criteria.type = StringSearchField.and("Project-Task")
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>
        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Task", "task 1"),
                tuple("Project-Task", "task 2"),
                tuple("Project-Task", "task 3"),
                tuple("Project-Task", "task 4"))
        assertThat(projectFollowingTickets.size).isEqualTo(4)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicketOfRisk() {
        val criteria = criteria
        criteria.type = StringSearchField.and("Project-Risk")
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>

        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Risk", "risk 1"),
                tuple("Project-Risk", "risk 2"),
                tuple("Project-Risk", "risk 3"),
                tuple("Project-Risk", "risk 4"))
        assertThat(projectFollowingTickets.size).isEqualTo(4)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetListProjectFollowingTicketOfBug() {
        val criteria = criteria
        criteria.type = StringSearchField.and("Project-Bug")
        val projectFollowingTickets = projectFollowingTicketService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<FollowingTicket>

        assertThat<FollowingTicket>(projectFollowingTickets).extracting("type", "name").contains(
                tuple("Project-Bug", "bug 1"),
                tuple("Project-Bug", "bug 2"),
                tuple("Project-Bug", "bug 3"),
                tuple("Project-Bug", "bug 4"))
        assertThat(projectFollowingTickets.size).isEqualTo(4)
    }

    @DataSet
    @Test
    @Throws(ParseException::class)
    fun testGetTotalCount() {
        assertThat(projectFollowingTicketService.getTotalCount(criteria)).isEqualTo(12)
    }
}
