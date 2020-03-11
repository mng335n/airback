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

import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.ProjectNotificationSetting;
import com.airback.module.project.service.ProjectNotificationSettingService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 2.0
 */
public class ProjectCustomPresenter extends AbstractPresenter<ProjectCustomView> {
    private static final long serialVersionUID = 1L;

    public ProjectCustomPresenter() {
        super(ProjectCustomView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        ProjectView projectView = (ProjectView) container;
        projectView.gotoSubView(ProjectView.CUSTOM_ENTRY, view);

        ProjectNotificationSettingService projectNotificationSettingService = AppContextUtil
                .getSpringBean(ProjectNotificationSettingService.class);
        ProjectNotificationSetting notification = projectNotificationSettingService
                .findNotification(UserUIContext.getUsername(), CurrentProjectVariables.getProjectId(),
                        AppUI.getAccountId());

        ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
        breadCrumb.gotoProjectSetting();
        view.showNotificationSettings(notification);
    }
}
