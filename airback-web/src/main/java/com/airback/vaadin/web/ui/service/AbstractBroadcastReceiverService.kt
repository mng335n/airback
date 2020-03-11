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
package com.airback.vaadin.web.ui.service

import com.google.common.eventbus.EventBus
import com.airback.cache.service.CacheService
import com.airback.core.AbstractNotification
import com.airback.core.BroadcastMessage
import com.airback.shell.event.ShellEvent
import com.airback.spring.AppContextUtil
import com.airback.vaadin.ui.airbackSession
import com.airback.web.DesktopApplication

/**
 * @author airback Ltd
 * @since 5.3.5
 */
abstract class AbstractBroadcastReceiverService : BroadcastReceiverService {

    protected lateinit var airbackApp: DesktopApplication

    override fun registerApp(airbackApp: DesktopApplication) {
        this.airbackApp = airbackApp
    }

    override fun broadcast(message: BroadcastMessage) {
        when {
            message.scope == BroadcastMessage.SCOPE_GLOBAL -> processMessage(message)
            message.sAccountId == null -> {
                // do nothing now
            }
            message.sAccountId == airbackApp.account.id -> when {
                message.targetUser != null && message.targetUser.equals(airbackApp.loggedInUser) -> processMessage(message)
                message.targetUser == null -> processMessage(message)
            }
        }
    }

    private fun processMessage(message: BroadcastMessage) {
        if (message.wrapObj is AbstractNotification) {
            val eventBus = airbackApp.getAttribute(airbackSession.EVENT_BUS_VAL) as EventBus?
            eventBus!!.post(ShellEvent.NewNotification(this, message.wrapObj))

            val cacheService = AppContextUtil.getSpringBean(CacheService::class.java)
            if (message.sAccountId != null) {
                cacheService.putValue(message.sAccountId.toString(), "notification", message.wrapObj)
            }
        } else {
            onBroadcast(message)
        }
    }

    protected abstract fun onBroadcast(message: BroadcastMessage)
}
