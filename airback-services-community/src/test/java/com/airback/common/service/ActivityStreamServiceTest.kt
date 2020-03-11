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
package com.airback.common.service

import com.airback.common.domain.SimpleActivityStream
import com.airback.common.domain.criteria.ActivityStreamSearchCriteria
import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.SetSearchField
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class, DbUnitInitializerRule::class)
class ActivityStreamServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var activityStreamService: ActivityStreamService

    @Test
    @DataSet
    fun testSearchActivityStreams() {
        val searchCriteria = ActivityStreamSearchCriteria()
        searchCriteria.moduleSet = SetSearchField("aa", "bb")
        searchCriteria.saccountid = NumberSearchField(1)

        val activities = activityStreamService.findPageableListByCriteria(BasicSearchRequest(searchCriteria))
        assertThat(activities.size).isEqualTo(3)
    }

    @Test
    @DataSet
    fun testQueryActivityWithComments() {
        val searchCriteria = ActivityStreamSearchCriteria()
        searchCriteria.moduleSet = SetSearchField("bb")
        searchCriteria.saccountid = NumberSearchField(1)

        val activities = activityStreamService.findPageableListByCriteria(BasicSearchRequest(searchCriteria)) as List<SimpleActivityStream>

        assertThat(activities.size).isEqualTo(1)
        assertThat<SimpleActivityStream>(activities).extracting("saccountid", "module", "action").contains(tuple(1, "bb", "update"))
    }
}
