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
package com.airback.module.project.view.client

import com.airback.common.UrlTokenizer
import com.airback.vaadin.EventBusFactory
import com.airback.module.project.event.ClientEvent
import com.airback.module.project.view.ProjectUrlResolver

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class ClientUrlResolver : ProjectUrlResolver() {
    init {
        this.addSubResolver("list", ListUrlResolver())
        this.addSubResolver("preview", PreviewUrlResolver())
        this.addSubResolver("add", AddUrlResolver())
        this.addSubResolver("edit", EditUrlResolver())
    }

    private class ListUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) = EventBusFactory.getInstance().post(ClientEvent.GotoList(this, null))
    }

    private class PreviewUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val clientId = token.getInt()
            EventBusFactory.getInstance().post(ClientEvent.GotoRead(this, clientId))
        }
    }

    private class AddUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            EventBusFactory.getInstance().post(ClientEvent.GotoAdd(this, null))
        }
    }

    private class EditUrlResolver : ProjectUrlResolver() {
        override fun handlePage(vararg params: String) {
            val token = UrlTokenizer(params[0])
            val clientId = token.getInt()
            EventBusFactory.getInstance().post(ClientEvent.GotoEdit(this, clientId))
        }
    }

    override fun handlePage(vararg params: String) {
        EventBusFactory.getInstance().post(ClientEvent.GotoList(this, null))
    }
}