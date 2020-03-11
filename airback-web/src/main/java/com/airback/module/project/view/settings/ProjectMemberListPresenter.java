/**
 * Copyright © airback
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.project.view.settings;

import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectMemberStatusConstants;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.criteria.ProjectMemberSearchCriteria;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectMemberListPresenter extends AbstractPresenter<ProjectMemberListView> {
    private static final long serialVersionUID = 1L;

    public ProjectMemberListPresenter() {
        super(ProjectMemberListView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.USERS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.USERS_ENTRY, view);

            ProjectMemberSearchCriteria criteria;
            if (data.getParams() == null) {
                criteria = new ProjectMemberSearchCriteria();
                criteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
                criteria.setStatuses(new SetSearchField<>(ProjectMemberStatusConstants.ACTIVE, ProjectMemberStatusConstants.NOT_ACCESS_YET));
                criteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
            } else {
                criteria = (ProjectMemberSearchCriteria) data.getParams();
            }
            view.setSearchCriteria(criteria);

            ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadCrumb.gotoUserList();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
