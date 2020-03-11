package com.airback.module.project.view.finance

import com.airback.module.project.domain.criteria.ItemTimeLoggingSearchCriteria
import com.airback.vaadin.mvp.PageView

/**
 * @author airback Ltd
 * @since 7.0.0
 */
interface ITimeTrackingContainer : PageView {
    fun setSearchCriteria(searchCriteria: ItemTimeLoggingSearchCriteria)
}