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
package com.airback.module.user.accountsettings.view

import com.airback.common.UrlTokenizer
import com.airback.vaadin.EventBusFactory
import com.airback.module.user.event.UserEvent
import com.airback.module.user.service.UserService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class UserUrlResolver : AccountSettingUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
        this.addSubResolver("add", AddUrlResolver())
        this.addSubResolver("bulkInvite", BulkInviteUrlResolver())
        this.addSubResolver("edit", EditUrlResolver())
        this.addSubResolver("preview", PreviewUrlResolver())
    }

    private class ListUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) =
                EventBusFactory.getInstance().post(UserEvent.GotoList(this, null))
    }

    private class BulkInviteUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) =
                EventBusFactory.getInstance().post(UserEvent.GotoBulkInvite(this, null))
    }

    private class AddUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) =
                EventBusFactory.getInstance().post(UserEvent.GotoAdd(this, null))
    }

    private class EditUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) {
            val username = UrlTokenizer(params[0]).getString()
            val userService = AppContextUtil.getSpringBean(UserService::class.java)
            val user = userService.findUserByUserNameInAccount(username, AppUI.accountId)
            EventBusFactory.getInstance().post(UserEvent.GotoEdit(this, user))
        }
    }

    private class PreviewUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) {
            val username = UrlTokenizer(params[0]).getString()
            EventBusFactory.getInstance().post(UserEvent.GotoRead(this, username))
        }
    }
}