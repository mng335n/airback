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

import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.StringSearchField
import com.airback.module.user.domain.SimpleUser
import com.airback.module.user.domain.criteria.UserSearchCriteria
import com.airback.module.user.service.UserService
import com.airback.test.DataSet
import com.airback.test.rule.DbUnitInitializerRule
import com.airback.test.spring.IntegrationServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class, DbUnitInitializerRule::class)
class UserServiceTest : IntegrationServiceTest() {

    @Autowired
    private lateinit var userService: UserService

    @DataSet
    @Test
    fun testGetListUser() {
        val criteria = UserSearchCriteria()
        criteria.saccountid = NumberSearchField(1)
        criteria.subDomain = StringSearchField.and("a")
        val users = userService.findPageableListByCriteria(BasicSearchRequest(criteria)) as List<SimpleUser>
        assertThat(users.size).isEqualTo(4)
        assertThat<SimpleUser>(users).extracting("username").contains(
                "anhminh@airback.com", "dieuha@airback.com",
                "anhle@airback.com", "test@airback.com")

        assertThat(userService.getTotalCount(criteria)).isEqualTo(4)
    }

    @DataSet
    @Test
    fun updateUserEmail() {
        val user = userService.findUserByUserNameInAccount("anhminh@airback.com", 1)
        assertThat(user!!.email).isEqualTo("anhminh@airback.com")

        user.email = "anhle@airback.com"
        userService.updateUserAccount(user, 1)

        val anotherUser = userService.findUserByUserNameInAccount("anhminh@airback.com", 1)
        assertThat(anotherUser).extracting("email", "lastname").contains("anhminh@airback.com", "Anh Minh")
    }

    @DataSet
    @Test
    fun testFindUserByUsernameInAccount() {
        val user = userService.findUserByUserNameInAccount("anhminh@airback.com", 1)
        assertThat(user).extracting("username", "accountId", "firstname", "lastname").contains("anhminh@airback.com", 1, "Anh", "Minh")
    }

    @Test
    @DataSet
    fun testAuthentication() {
        val user = userService.authentication("a@airback.com", "aa", "b", true)
        assertThat(user).extracting("username", "subDomain", "accountId").contains("a@airback.com", "b", 2)
    }
}
