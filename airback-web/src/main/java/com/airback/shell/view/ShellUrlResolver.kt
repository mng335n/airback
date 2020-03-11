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
package com.airback.shell.view

import com.airback.core.utils.StringUtils
import com.airback.module.project.view.ProjectUrlResolver
import com.airback.module.user.accountsettings.view.AccountSettingUrlResolver
import com.airback.vaadin.mvp.UrlResolver

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class ShellUrlResolver : UrlResolver() {
    companion object {
        @JvmField
        val ROOT = ShellUrlResolver()
    }

    init {
        this.addSubResolver("project", ProjectUrlResolver().build())
        this.addSubResolver("account", AccountSettingUrlResolver().build())
    }

    fun resolveFragment(newFragmentUrl: String?) {
        if (!StringUtils.isBlank(newFragmentUrl)) {
            val tokens = newFragmentUrl!!.split("/").toTypedArray()
            this.handle(*tokens)
        }
    }

    override fun defaultPageErrorHandler() {}
}