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
package com.airback.module.project.view.settings;

import com.airback.core.airbackException;
import com.airback.module.project.view.ProjectView;
import com.airback.vaadin.EventBusFactory;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.ProjectMember;
import com.airback.module.project.domain.SimpleProjectMember;
import com.airback.module.project.event.ProjectMemberEvent;
import com.airback.module.project.service.ProjectMemberService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.IEditFormHandler;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectMemberEditPresenter extends AbstractPresenter<ProjectMemberEditView> {
    private static final long serialVersionUID = 1L;

    public ProjectMemberEditPresenter() {
        super(ProjectMemberEditView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new IEditFormHandler<SimpleProjectMember>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(SimpleProjectMember projectMember) {
                saveProjectMember(projectMember);
                EventBusFactory.getInstance().post(new ProjectMemberEvent.GotoList(this, projectMember.getProjectid()));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new ProjectMemberEvent.GotoList(this, CurrentProjectVariables.getProjectId()));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.USERS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.SETTING, view);

            SimpleProjectMember member = (SimpleProjectMember) data.getParams();
            view.editItem(member);

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            if (member.getId() == null) {
                breadcrumb.gotoUserAdd();
            } else {
                breadcrumb.gotoUserEdit(member);
            }
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }

    }

    private void saveProjectMember(ProjectMember projectMember) {
        ProjectMemberService projectMemberService = AppContextUtil.getSpringBean(ProjectMemberService.class);

        if (projectMember.getId() == null) {
            throw new airbackException("User not exist in projectMember table, something goes wrong in DB");
        } else {
            projectMemberService.updateWithSession(projectMember, UserUIContext.getUsername());
        }
    }

}
