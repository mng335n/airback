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

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.airbackException;
import com.airback.db.arguments.NumberSearchField;
import com.airback.module.project.view.ProjectView;
import com.airback.vaadin.EventBusFactory;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.SimpleProjectRole;
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria;
import com.airback.module.project.event.ProjectRoleEvent;
import com.airback.module.project.i18n.ProjectMemberI18nEnum;
import com.airback.module.project.service.ProjectRoleService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.DefaultPreviewFormHandler;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectRoleReadPresenter extends AbstractPresenter<ProjectRoleReadView> {
    private static final long serialVersionUID = 1L;

    private ProjectRoleService projectRoleService = AppContextUtil.getSpringBean(ProjectRoleService.class);

    public ProjectRoleReadPresenter() {
        super(ProjectRoleReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<SimpleProjectRole>() {
            @Override
            public void onEdit(SimpleProjectRole data) {
                EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoEdit(this, data));
            }

            @Override
            public void onAdd(SimpleProjectRole data) {
                EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoAdd(this, null));
            }

            @Override
            public void onDelete(final SimpleProjectRole role) {
                if (Boolean.FALSE.equals(role.getIssystemrole())) {
                    ConfirmDialogExt.show(UI.getCurrent(),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                            confirmDialog -> {
                                if (confirmDialog.isConfirmed()) {
                                    projectRoleService.removeWithSession(role, UserUIContext.getUsername(), AppUI.getAccountId());
                                    EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoList(this, null));
                                }
                            });
                } else {
                    NotificationUtil.showErrorNotification(UserUIContext
                            .getMessage(ProjectMemberI18nEnum.CAN_NOT_DELETE_ROLE_MESSAGE, role.getRolename()));
                }
            }

            @Override
            public void onClone(SimpleProjectRole data) {
                SimpleProjectRole cloneData = (SimpleProjectRole) data.copy();
                cloneData.setRolename(null);
                EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoAdd(this, cloneData));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoList(this, null));
            }

            @Override
            public void gotoNext(SimpleProjectRole data) {
                ProjectRoleSearchCriteria criteria = new ProjectRoleSearchCriteria();
                criteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.GREATER));
                Integer nextId = projectRoleService.getNextItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoLastRecordNotification();
                }
            }

            @Override
            public void gotoPrevious(SimpleProjectRole data) {
                ProjectRoleSearchCriteria criteria = new ProjectRoleSearchCriteria();
                criteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.LESS_THAN));
                Integer nextId = projectRoleService.getPreviousItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoFirstRecordNotification();
                }
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        ProjectView projectView = (ProjectView) container;

        if (data.getParams() instanceof Integer) {
            SimpleProjectRole role = projectRoleService.findById((Integer) data.getParams(), AppUI.getAccountId());
            if (role == null) {
                NotificationUtil.showRecordNotExistNotification();
            } else {
                projectView.gotoSubView(ProjectView.ROLE_ENTRY, view);
                view.previewItem(role);
                ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
                breadCrumb.gotoRoleRead(role);
            }
        } else {
            throw new airbackException("Do not support screen data: " + data);
        }
    }
}
