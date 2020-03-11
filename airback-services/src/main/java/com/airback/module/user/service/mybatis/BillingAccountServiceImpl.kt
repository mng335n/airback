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
package com.airback.module.user.service.mybatis

import com.google.common.eventbus.AsyncEventBus
import com.airback.configuration.EnDecryptHelper
import com.airback.configuration.IDeploymentMode
import com.airback.core.UserInvalidInputException
import com.airback.core.utils.StringUtils
import com.airback.db.arguments.NumberSearchField
import com.airback.db.arguments.SetSearchField
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import com.airback.module.billing.RegisterStatusConstants
import com.airback.module.billing.UserStatusConstants
import com.airback.module.billing.esb.AccountCreatedEvent
import com.airback.module.user.dao.*
import com.airback.module.user.domain.*
import com.airback.module.user.domain.criteria.UserSearchCriteria
import com.airback.module.user.esb.SendUserEmailVerifyRequestEvent
import com.airback.module.user.service.BillingAccountService
import com.airback.module.user.service.RoleService
import com.airback.security.PermissionMap
import org.apache.commons.collections.CollectionUtils
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class BillingAccountServiceImpl(private val billingAccountMapper: BillingAccountMapper,
                                private val billingAccountMapperExt: BillingAccountMapperExt,
                                private val asyncEventBus: AsyncEventBus,
                                private val userMapper: UserMapper,
                                private val userMapperExt: UserMapperExt,
                                private val userAccountMapper: UserAccountMapper,
                                private val roleService: RoleService,
                                private val deploymentMode: IDeploymentMode) : DefaultCrudService<Int, BillingAccount>(), BillingAccountService {

    override val crudMapper: ICrudGenericDAO<Int, BillingAccount>
        get() = billingAccountMapper as ICrudGenericDAO<Int, BillingAccount>

    override fun getBillingAccountById(accountId: Int): SimpleBillingAccount? =
            billingAccountMapperExt.getBillingAccountById(accountId)

    override fun updateSelectiveWithSession(record: BillingAccount, username: String?): Int? = try {
        super.updateSelectiveWithSession(record, username)
    } catch (e: DuplicateKeyException) {
        throw UserInvalidInputException("The domain ${record.subdomain} is already used")
    }

    override fun getAccountByDomain(domain: String): SimpleBillingAccount? =
            if (deploymentMode.isDemandEdition) {
                billingAccountMapperExt.getAccountByDomain(domain)
            } else {
                billingAccountMapperExt.defaultAccountByDomain
            }

    override fun getAccountById(accountId: Int): BillingAccount? {
        val ex = BillingAccountExample()

        if (deploymentMode.isDemandEdition) {
            ex.createCriteria().andIdEqualTo(accountId)
        }

        val accounts = billingAccountMapper.selectByExample(ex)
        return if (accounts.isEmpty()) null else accounts[0]
    }

    override fun createDefaultAccountData(username: String, password: String, timezoneId: String, language: String, isEmailVerified: Boolean, isCreatedDefaultData: Boolean, sAccountId: Int) {
        // Check whether user has registered to the system before
        val encryptedPassword = EnDecryptHelper.encryptSaltPassword(password)
        val ex = UserExample()
        ex.createCriteria().andUsernameEqualTo(username)
        val users = userMapper.selectByExample(ex)

        val now = LocalDateTime.now()

        if (CollectionUtils.isNotEmpty(users)) {
            val existUser = users.filter { encryptedPassword != it.password }.count() > 0
            if (existUser) {
                throw UserInvalidInputException("There is already user $username in the airback database. If it is yours, you must enter the same password you registered to airback. Otherwise " +
                        "you must use the different email.")
            }
        } else {
            // Register the new user to this account
            val user = User()
            user.email = username
            user.password = encryptedPassword
            user.timezone = timezoneId
            user.username = username
            user.registeredtime = now
            user.lastaccessedtime = now
            user.language = language

            if (isEmailVerified) {
                user.status = UserStatusConstants.EMAIL_VERIFIED
            } else {
                user.status = UserStatusConstants.EMAIL_NOT_VERIFIED
            }

            if (user.firstname == null) {
                user.firstname = ""
            }

            if (StringUtils.isBlank(user.lastname)) {
                user.lastname = StringUtils.extractNameFromEmail(username)
            }
            userMapper.insert(user)
            if (!isEmailVerified && deploymentMode.isDemandEdition) {
                asyncEventBus.post(SendUserEmailVerifyRequestEvent(sAccountId, user))
            }
        }

        // save default roles
        saveEmployeeRole(sAccountId)
        val adminRoleId = saveAdminRole(sAccountId)
        saveGuestRole(sAccountId)

        // save user account
        val userAccount = UserAccount()
        userAccount.accountid = sAccountId
        userAccount.isaccountowner = true
        userAccount.registeredtime = now
        userAccount.registerstatus = RegisterStatusConstants.ACTIVE
        userAccount.registrationsource = "Web"
        userAccount.roleid = adminRoleId
        userAccount.username = username

        userAccountMapper.insert(userAccount)
        asyncEventBus.post(AccountCreatedEvent(sAccountId, username, isCreatedDefaultData))
    }

    override fun getTotalActiveUsersInAccount(accountId: Int): Int {
        val criteria = UserSearchCriteria()
        criteria.registerStatuses = SetSearchField(RegisterStatusConstants.ACTIVE)
        criteria.saccountid = NumberSearchField(accountId)
        return userMapperExt.getTotalCount(criteria)
    }

    private fun saveEmployeeRole(accountId: Int): Int {
        // Register default role for account
        val role = Role()
        role.rolename = SimpleRole.EMPLOYEE
        role.description = ""
        role.saccountid = accountId
        role.issystemrole = true
        role.isdefault = java.lang.Boolean.FALSE
        val roleId = roleService.saveWithSession(role, "")
        roleService.savePermission(roleId, PermissionMap.EMPLOYEE_ROLE_MAP, accountId)
        return roleId
    }

    private fun saveAdminRole(accountId: Int): Int {
        // Register default role for account
        val role = Role()
        role.rolename = SimpleRole.ADMIN
        role.description = ""
        role.saccountid = accountId
        role.issystemrole = true
        role.isdefault = java.lang.Boolean.FALSE
        val roleId = roleService.saveWithSession(role, "")
        roleService.savePermission(roleId, PermissionMap.ADMIN_ROLE_MAP, accountId)
        return roleId
    }

    private fun saveGuestRole(accountId: Int): Int {
        // Register default role for account
        val role = Role()
        role.rolename = SimpleRole.GUEST
        role.description = ""
        role.saccountid = accountId
        role.issystemrole = true
        role.isdefault = java.lang.Boolean.TRUE
        val roleId = roleService.saveWithSession(role, "")
        roleService.savePermission(roleId, PermissionMap.GUESS_ROLE_MAP, accountId)
        return roleId
    }
}
