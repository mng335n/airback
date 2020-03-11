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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.reporting.configuration

import com.airback.module.user.AccountLinkGenerator
import com.airback.module.user.domain.Role
import com.airback.module.user.domain.SimpleRole
import com.airback.module.user.domain.SimpleUser
import com.airback.module.user.domain.User
import com.airback.reporting.ColumnBuilderClassMapper
import com.airback.reporting.expression.DateExpression
import com.airback.reporting.expression.PrimaryTypeFieldExpression
import com.airback.reporting.generator.ComponentBuilderGenerator
import com.airback.reporting.generator.HyperlinkBuilderGenerator
import com.airback.reporting.generator.SimpleExpressionBuilderGenerator
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression
import net.sf.dynamicreports.report.definition.ReportParameters
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author airback Ltd
 * @since 5.2.12
 */
@Component
class AccountColumnBuilderMapper : InitializingBean {
    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        ColumnBuilderClassMapper.put(SimpleUser::class.java, buildUserMap())
        ColumnBuilderClassMapper.put(SimpleRole::class.java, buildRoleMap())
    }

    private fun buildUserMap(): Map<String, ComponentBuilderGenerator> {
        val map = HashMap<String, ComponentBuilderGenerator>()

        val userNameExpr = PrimaryTypeFieldExpression<String>(SimpleUser.Field.displayName.name)
        val userHrefExpr = object : AbstractSimpleExpression<String>() {

            override fun evaluate(reportParameters: ReportParameters): String {
                val username = reportParameters.getFieldValue<String>(User.Field.username.name)
                val siteUrl = reportParameters.getParameterValue<String>("siteUrl")
                return AccountLinkGenerator.generatePreviewFullUserLink(siteUrl, username)
            }
        }
        map[User.Field.username.name] = HyperlinkBuilderGenerator(userNameExpr, userHrefExpr)

        val roleNameExpr = PrimaryTypeFieldExpression<String>(SimpleUser.Field.roleName.name)
        val roleHrefExpr = object : AbstractSimpleExpression<String>() {

            override fun evaluate(reportParameters: ReportParameters): String {
                val roleId = reportParameters.getFieldValue<Int>(SimpleUser.Field.roleId.name)
                val siteUrl = reportParameters.getParameterValue<String>("siteUrl")
                return AccountLinkGenerator.generatePreviewFullRoleLink(siteUrl, roleId)
            }
        }
        map[SimpleUser.Field.roleId.name] = HyperlinkBuilderGenerator(roleNameExpr, roleHrefExpr)
        map[User.Field.birthday.name] = SimpleExpressionBuilderGenerator(DateExpression(User.Field.birthday.name))
        return map
    }

    private fun buildRoleMap(): Map<String, ComponentBuilderGenerator> {
        val map = HashMap<String, ComponentBuilderGenerator>()
        val roleNameExpr = PrimaryTypeFieldExpression<String>(Role.Field.rolename.name)
        val roleHrefExpr = object : AbstractSimpleExpression<String>() {

            override fun evaluate(reportParameters: ReportParameters): String {
                val roleId = reportParameters.getFieldValue<Int>(Role.Field.id.name)
                val siteUrl = reportParameters.getParameterValue<String>("siteUrl")
                return AccountLinkGenerator.generatePreviewFullRoleLink(siteUrl, roleId)
            }
        }
        map[Role.Field.rolename.name] = HyperlinkBuilderGenerator(roleNameExpr, roleHrefExpr)
        return map
    }
}
