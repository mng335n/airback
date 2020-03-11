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

import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.event.BugVersionEvent;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
import com.airback.module.project.domain.Version;
import com.airback.module.project.service.VersionService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.IEditFormHandler;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class VersionAddPresenter extends AbstractPresenter<VersionAddView> {
    private static final long serialVersionUID = 1L;

    public VersionAddPresenter() {
        super(VersionAddView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new IEditFormHandler<Version>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(Version item) {
                save(item);
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoList(this, null));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoList(this, null));
            }

            @Override
            public void onSaveAndNew(final Version item) {
                save(item);
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoAdd(this, null));
            }
        });
    }

    private void save(Version item) {
        VersionService versionService = AppContextUtil.getSpringBean(VersionService.class);

        if (item.getId() == null) {
            versionService.saveWithSession(item, UserUIContext.getUsername());
        } else {
            versionService.updateWithSession(item, UserUIContext.getUsername());
        }
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.VERSIONS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.VERSION_ENTRY, view);
            Version version = (Version) data.getParams();

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            if (version.getId() == null) {
                version.setSaccountid(AppUI.getAccountId());
                version.setProjectid(CurrentProjectVariables.getProjectId());
                version.setStatus(StatusI18nEnum.Open.name());
                breadcrumb.gotoVersionAdd();
            } else {
                breadcrumb.gotoVersionEdit(version);
            }
            view.editItem(version);
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
