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
package com.airback.module.user.ui.format

import com.airback.common.i18n.GenericI18Enum
import com.airback.core.utils.StringUtils.isBlank
import com.airback.html.FormatUtils
import com.airback.module.mail.MailUtils
import com.airback.module.user.AccountLinkGenerator
import com.airback.module.user.domain.SimpleUser
import com.airback.module.user.service.UserService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.ui.formatter.HistoryFieldFormat

/**
 * @author airback Ltd.
 * @since 4.0
 */
class UserHistoryFieldFormat : HistoryFieldFormat {

    override fun toString(value: String): String =
            toString(UserUIContext.getUser(), value, true, UserUIContext.getMessage(GenericI18Enum.FORM_EMPTY))

    override fun toString(currentViewUser: SimpleUser, value: String, displayAsHtml: Boolean, msgIfBlank: String): String {
        if (isBlank(value)) {
            return msgIfBlank
        }

        val userService = AppContextUtil.getSpringBean(UserService::class.java)
        val user = userService.findUserByUserNameInAccount(value, currentViewUser.accountId!!)
        if (user != null) {
            return if (displayAsHtml) {
                val userAvatarLink = MailUtils.getAvatarLink(user.avatarid, 16)
                val img = FormatUtils.newImg("avatar", userAvatarLink)

                val userLink = AccountLinkGenerator.generatePreviewFullUserLink(
                        MailUtils.getSiteUrl(AppUI.accountId), user.username)

                val link = FormatUtils.newA(userLink, user.displayName!!)
                FormatUtils.newLink(img, link).write()
            } else user.displayName!!
        }

        return value
    }
}
