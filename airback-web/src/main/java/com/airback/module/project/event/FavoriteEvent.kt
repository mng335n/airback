package com.airback.module.project.event

import com.airback.vaadin.event.ApplicationEvent

/**
 * @author airback Ltd
 * @since 7.0.0
 */
object FavoriteEvent {

    class GotoList(source: Any, val data: Any?) : ApplicationEvent(source)
}