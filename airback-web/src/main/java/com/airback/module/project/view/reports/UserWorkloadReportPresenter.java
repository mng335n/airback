package com.airback.module.project.view.reports;

import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 5.3.0
 */
public class UserWorkloadReportPresenter extends AbstractPresenter<UserWorkloadReportView> {
    public UserWorkloadReportPresenter() {
        super(UserWorkloadReportView.class);
    }

    @Override
    protected void viewAttached() {
        view.getSearchHandlers().addSearchHandler(this::doSearch);
    }

    public void doSearch(ProjectTicketSearchCriteria searchCriteria) {
        view.queryTickets(searchCriteria);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        IReportContainer reportContainer = (IReportContainer) container;
        reportContainer.addView(view);

        view.display();

        ReportBreadcrumb breadCrumb = ViewManager.getCacheComponent(ReportBreadcrumb.class);
        breadCrumb.gotoUserWorkloadReport();
    }
}
