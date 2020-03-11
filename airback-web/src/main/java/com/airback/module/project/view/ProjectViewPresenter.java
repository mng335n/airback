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
package com.airback.module.project.view;

import com.airback.core.ResourceNotFoundException;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.UserNotBelongProjectException;
import com.airback.module.project.domain.SimpleProject;
import com.airback.module.project.service.ProjectMemberService;
import com.airback.module.project.service.ProjectService;
import com.airback.module.project.view.user.ProjectDashboardPresenter;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.IPresenter;
import com.airback.vaadin.mvp.PageActionChain;
import com.airback.vaadin.mvp.PresenterResolver;
import com.airback.vaadin.mvp.ScreenData;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectViewPresenter extends ProjectGenericPresenter<ProjectView> {
    private static final long serialVersionUID = 1L;

    public ProjectViewPresenter() {
        super(ProjectView.class);
    }

    @Override
    public void onGo(HasComponents container, ScreenData<?> data) {
        ProjectModule prjContainer = (ProjectModule) container;
        prjContainer.setContent(view);
        if (data.getParams() instanceof Integer) {
            ProjectService projectService = AppContextUtil.getSpringBean(ProjectService.class);
            SimpleProject project = projectService.findById((Integer) data.getParams(), AppUI.getAccountId());

            if (project == null) {
                throw new ResourceNotFoundException();
            } else {
                ProjectMemberService projectMemberService = AppContextUtil.getSpringBean(ProjectMemberService.class);
                boolean userBelongToProject = projectMemberService.isUserBelongToProject(UserUIContext.getUsername(), project.getId(),
                        AppUI.getAccountId());
                if (userBelongToProject || UserUIContext.isAdmin()) {
                    CurrentProjectVariables.setProject(project);
                    view.initView(project);
                } else {
                    throw new UserNotBelongProjectException();
                }
            }
        }
    }

    @Override
    protected void onHandleChain(HasComponents container, PageActionChain pageActionChain) {
        ScreenData<?> pageAction = pageActionChain.peek();

        Class<? extends IPresenter> presenterCls = ProjectPresenterDataMapper.presenter(pageAction);
        if (presenterCls != null) {
            IPresenter<?> presenter = PresenterResolver.getPresenter(presenterCls);
            presenter.handleChain(view, pageActionChain);
        } else {
            throw new UnsupportedOperationException("Not support page action chain " + pageAction);
        }
    }

    @Override
    protected void onDefaultStopChain() {
        ProjectDashboardPresenter presenter = PresenterResolver.getPresenter(ProjectDashboardPresenter.class);
        presenter.go(view, null);
    }
}
