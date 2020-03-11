package com.airback.module.project.view.ticket;

import com.airback.core.SecureAccessException;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 5.1.1
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class TicketKanbanBoardPresenter extends ProjectGenericPresenter<TicketKanbanBoardView> implements ITicketKanbanPresenter {

    public TicketKanbanBoardPresenter() {
        super(TicketKanbanBoardView.class);
    }

    @Override
    protected void postInitView() {
        view.getSearchHandlers().addSearchHandler(this::doSearch);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.TASKS) || CurrentProjectVariables.canRead(ProjectRolePermissionCollections.RISKS)
                || CurrentProjectVariables.canRead(ProjectRolePermissionCollections.BUGS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.KANBAN_ENTRY, view);
            view.display();

            ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadCrumb.gotoTicketKanbanView();
        } else {
            throw new SecureAccessException();
        }
    }

    private void doSearch(ProjectTicketSearchCriteria searchCriteria) {
        view.queryTickets(searchCriteria);
    }
}
