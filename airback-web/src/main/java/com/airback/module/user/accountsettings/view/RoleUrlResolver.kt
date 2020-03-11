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
import com.airback.module.user.event.RoleEvent
import com.airback.module.user.service.RoleService
import com.airback.spring.AppContextUtil
import com.airback.vaadin.AppUI

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class RoleUrlResolver : AccountSettingUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
        this.addSubResolver("add", AddUrlResolver())
        this.addSubResolver("edit", EditUrlResolver())
        this.addSubResolver("preview", PreviewUrlResolver())
    }

    private class ListUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) =
                EventBusFactory.getInstance().post(RoleEvent.GotoList(this, null))
    }

    private class AddUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) =
                EventBusFactory.getInstance().post(RoleEvent.GotoAdd(this, null))
    }

    private class EditUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) {
            val roleId = UrlTokenizer(params[0]).getInt()
            val roleService = AppContextUtil.getSpringBean(RoleService::class.java)
            val role = roleService.findById(roleId, AppUI.accountId)
            EventBusFactory.getInstance().post(RoleEvent.GotoEdit(this, role))
        }
    }

    private class PreviewUrlResolver : AccountSettingUrlResolver() {
        override fun handlePage(vararg params: String) {
            val roleId = UrlTokenizer(params[0]).getInt()
            EventBusFactory.getInstance().post(RoleEvent.GotoRead(this, roleId))
        }
    }
}