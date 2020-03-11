package com.airback.vaadin.event

/**
 * @author airback Ltd
 * @since 7.0.0
 */
class ViewEvent<B>(source: Any, val data: B) : ApplicationEvent(source) {
    companion object {
        const val VIEW_IDENTIFIER = "viewEvent"
    }
}