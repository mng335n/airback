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
package com.airback.module.project.view.milestone;

import com.airback.core.SecureAccessException;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 5.2.0
 */
public class MilestoneRoadmapPresenter extends ProjectGenericPresenter<MilestoneRoadmapView> {
    public MilestoneRoadmapPresenter() {
        super(MilestoneRoadmapView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.MILESTONES)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.MILESTONE_ENTRY, view);

            view.lazyLoadView();
            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoRoadmap();
        } else {
            throw new SecureAccessException();
        }
    }
}
