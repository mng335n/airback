package com.airback.module.project.view.reports;

import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.vaadin.event.HasSearchHandlers;
import com.airback.vaadin.mvp.PageView;

/**
 * @author airback Ltd
 * @since 5.3.0
 */
public interface UserWorkloadReportView extends PageView {

    void display();

    void queryTickets(ProjectTicketSearchCriteria searchCriteria);

    ProjectTicketSearchCriteria getCriteria();

    HasSearchHandlers<ProjectTicketSearchCriteria> getSearchHandlers();
}
