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
package com.airback.module.mail

import com.airback.configuration.IDeploymentMode
import com.airback.module.file.service.AbstractStorageService
import com.airback.module.user.service.BillingAccountService
import com.airback.spring.AppContextUtil

/**
 * @author airback Ltd
 * @since 6.0.0
 */
object MailUtils {
    @JvmStatic
    fun getSiteUrl(sAccountId: Int): String {
        var siteUrl = ""
        val mode = AppContextUtil.getSpringBean(IDeploymentMode::class.java)
        if (mode.isDemandEdition) {
            val billingAccountService = AppContextUtil.getSpringBean(BillingAccountService::class.java)
            val account = billingAccountService.getAccountById(sAccountId)
            if (account != null) siteUrl = mode.getSiteUrl(account.subdomain)
        } else siteUrl = mode.getSiteUrl("")
        return siteUrl
    }

    @JvmStatic
    fun getAvatarLink(userAvatarId: String?, size: Int) = AppContextUtil.getSpringBean(AbstractStorageService::class.java).getAvatarPath(userAvatarId, size)
}