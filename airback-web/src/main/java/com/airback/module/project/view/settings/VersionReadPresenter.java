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

import com.airback.core.airbackException;
import com.airback.db.arguments.NumberSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.event.BugVersionEvent;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
import com.airback.module.project.domain.Version;
import com.airback.module.project.domain.criteria.VersionSearchCriteria;
import com.airback.module.project.service.VersionService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.DefaultPreviewFormHandler;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.airback.vaadin.reporting.FormReportLayout;
import com.airback.vaadin.reporting.PrintButton;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class VersionReadPresenter extends AbstractPresenter<VersionReadView> {
    private static final long serialVersionUID = 1L;

    public VersionReadPresenter() {
        super(VersionReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<Version>() {
            @Override
            public void onEdit(Version data) {
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoEdit(this, data));
            }

            @Override
            public void onAdd(Version data) {
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoAdd(this, null));
            }

            @Override
            public void onDelete(Version data) {
                VersionService versionService = AppContextUtil.getSpringBean(VersionService.class);
                versionService.removeWithSession(data, UserUIContext.getUsername(), AppUI.getAccountId());
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoList(this, null));
            }

            @Override
            public void onClone(Version data) {
                Version cloneData = (Version) data.copy();
                cloneData.setId(null);
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoEdit(this, cloneData));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new BugVersionEvent.GotoList(this, null));
            }

            @Override
            public void onPrint(Object source, Version data) {
                PrintButton btn = (PrintButton) source;
                btn.doPrint(data, new FormReportLayout(ProjectTypeConstants.VERSION, Version.Field.name.name(),
                        VersionDefaultFormLayoutFactory.getAddForm(), Version.Field.id.name()));
            }

            @Override
            public void gotoNext(Version data) {
                VersionService componentService = AppContextUtil.getSpringBean(VersionService.class);
                VersionSearchCriteria criteria = new VersionSearchCriteria();
                criteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.GREATER));
                Integer nextId = componentService.getNextItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new BugVersionEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoLastRecordNotification();
                }
            }

            @Override
            public void gotoPrevious(Version data) {
                VersionService componentService = AppContextUtil.getSpringBean(VersionService.class);
                VersionSearchCriteria criteria = new VersionSearchCriteria();
                criteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.LESS_THAN));
                Integer nextId = componentService.getPreviousItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new BugVersionEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoFirstRecordNotification();
                }
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.VERSIONS)) {
            if (data.getParams() instanceof Integer) {
                VersionService componentService = AppContextUtil.getSpringBean(VersionService.class);
                Version version = componentService.findById((Integer) data.getParams(), AppUI.getAccountId());
                if (version != null) {
                    ProjectView projectView = (ProjectView) container;
                    projectView.gotoSubView(ProjectView.VERSION_ENTRY, view);
                    view.previewItem(version);

                    ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
                    breadcrumb.gotoVersionRead(version);
                } else {
                    NotificationUtil.showRecordNotExistNotification();
                }
            } else {
                throw new airbackException("Unhanddle this case yet");
            }
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

}
