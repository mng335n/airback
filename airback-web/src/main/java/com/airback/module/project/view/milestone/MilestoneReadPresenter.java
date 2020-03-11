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
package com.airback.module.project.view.milestone;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.airbackException;
import com.airback.core.ResourceNotFoundException;
import com.airback.core.SecureAccessException;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.Milestone;
import com.airback.module.project.domain.SimpleMilestone;
import com.airback.module.project.domain.criteria.MilestoneSearchCriteria;
import com.airback.module.project.event.MilestoneEvent;
import com.airback.module.project.service.MilestoneService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
import com.airback.module.project.view.ProjectView;
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
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class MilestoneReadPresenter extends ProjectGenericPresenter<MilestoneReadView> {
    private static final long serialVersionUID = 1L;

    public MilestoneReadPresenter() {
        super(MilestoneReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<SimpleMilestone>() {
            @Override
            public void onEdit(SimpleMilestone data) {
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoEdit(this, data));
            }

            @Override
            public void onAdd(SimpleMilestone data) {
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoAdd(this, null));
            }

            @Override
            public void onDelete(final SimpleMilestone data) {
                ConfirmDialogExt.show(UI.getCurrent(),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
                                milestoneService.removeWithSession(data, UserUIContext.getUsername(), AppUI.getAccountId());
                                EventBusFactory.getInstance().post(new MilestoneEvent.GotoList(this, null));
                            }
                        });
            }

            @Override
            public void onPrint(Object source, SimpleMilestone data) {
                PrintButton btn = (PrintButton) source;
                btn.doPrint(data, new FormReportLayout(ProjectTypeConstants.MILESTONE, Milestone.Field.name.name(),
                        MilestoneDefaultFormLayoutFactory.getAddForm(), Milestone.Field.id.name(),
                        Milestone.Field.saccountid.name()));
            }

            @Override
            public void onClone(SimpleMilestone data) {
                SimpleMilestone cloneData = (SimpleMilestone) data.copy();
                cloneData.setId(null);
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoEdit(this, cloneData));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoList(this, null));
            }

            @Override
            public void gotoNext(SimpleMilestone data) {
                MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
                MilestoneSearchCriteria criteria = new MilestoneSearchCriteria();
                criteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.GREATER));
                Integer nextId = milestoneService.getNextItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new MilestoneEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoLastRecordNotification();
                }

            }

            @Override
            public void gotoPrevious(SimpleMilestone data) {
                MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
                MilestoneSearchCriteria criteria = new MilestoneSearchCriteria();
                criteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
                criteria.setId(new NumberSearchField(data.getId(), NumberSearchField.LESS_THAN));
                Integer nextId = milestoneService.getPreviousItemKey(criteria);
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new MilestoneEvent.GotoRead(this, nextId));
                } else {
                    NotificationUtil.showGotoFirstRecordNotification();
                }
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.MILESTONES)) {
            ProjectView projectView = (ProjectView) container;

            if (data.getParams() instanceof Integer) {
                MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
                SimpleMilestone milestone = milestoneService.findById((Integer) data.getParams(), AppUI.getAccountId());
                if (milestone != null) {
                    projectView.gotoSubView(ProjectView.MILESTONE_ENTRY, view);
                    view.previewItem(milestone);

                    ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
                    breadcrumb.gotoMilestoneRead(milestone);
                } else {
                    throw new ResourceNotFoundException();
                }
            } else {
                throw new airbackException("Unhandle this case yet");
            }
        } else {
            throw new SecureAccessException();
        }
    }
}
