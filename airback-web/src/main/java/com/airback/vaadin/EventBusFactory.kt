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
package com.airback.vaadin

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionContext
import com.google.common.eventbus.SubscriberExceptionHandler
import com.airback.shell.event.ShellEvent
import com.airback.vaadin.ui.airbackSession
import org.slf4j.LoggerFactory

/**
 * @author airback Ltd
 * @since 6.0.0
 */
object EventBusFactory {
    private val LOG = LoggerFactory.getLogger(EventBusFactory::class.java)

    @JvmStatic fun getInstance(): EventBus {
        var eventBus = airbackSession.getCurrentUIVariable(airbackSession.EVENT_BUS_VAL) as EventBus?
        if (eventBus == null) {
            eventBus = EventBus(SubscriberEventBusExceptionHandler())
            airbackSession.putCurrentUIVariable(airbackSession.EVENT_BUS_VAL, eventBus)
        }
        return eventBus
    }

    private class SubscriberEventBusExceptionHandler : SubscriberExceptionHandler {
        override fun handleException(e: Throwable, context: SubscriberExceptionContext) {
            getInstance().post(ShellEvent.NotifyErrorEvent(this, e))
        }
    }
}