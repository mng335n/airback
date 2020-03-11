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
package com.airback.module.project.view.bug;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.ResourceNotFoundException;
import com.airback.core.SecureAccessException;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.BugWithBLOBs;
import com.airback.module.project.domain.SimpleBug;
import com.airback.module.project.event.BugEvent;
import com.airback.module.project.event.TicketEvent;
import com.airback.module.project.service.BugService;
import com.airback.module.project.service.TicketKeyService;
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
public class BugReadPresenter extends ProjectGenericPresenter<BugReadView> {
    private static final long serialVersionUID = 1L;

    public BugReadPresenter() {
        super(BugReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<SimpleBug>() {
            @Override
            public void onEdit(SimpleBug data) {
                EventBusFactory.getInstance().post(new BugEvent.GotoEdit(this, data));
            }

            @Override
            public void onAdd(SimpleBug data) {
                EventBusFactory.getInstance().post(new BugEvent.GotoAdd(this, null));
            }

            @Override
            public void onDelete(SimpleBug data) {
                ConfirmDialogExt.show(UI.getCurrent(),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                BugService bugService = AppContextUtil.getSpringBean(BugService.class);
                                bugService.removeWithSession(data, UserUIContext.getUsername(), AppUI.getAccountId());
                                EventBusFactory.getInstance().post(new TicketEvent.GotoDashboard(this, null));
                            }
                        });
            }

            @Override
            public void onPrint(Object source, SimpleBug data) {
                PrintButton btn = (PrintButton) source;
                btn.doPrint(data, new FormReportLayout(ProjectTypeConstants.BUG, BugWithBLOBs.Field.name.name(),
                        BugDefaultFormLayoutFactory.getReadForm(), SimpleBug.Field.components.name(),
                        SimpleBug.Field.affectedVersions.name(), SimpleBug.Field.fixedVersions.name(),
                        BugWithBLOBs.Field.id.name(), SimpleBug.Field.selected.name()));
            }

            @Override
            public void onClone(SimpleBug data) {
                SimpleBug cloneData = (SimpleBug) data.copy();
                cloneData.setId(null);
                EventBusFactory.getInstance().post(new BugEvent.GotoEdit(this, cloneData));
            }

            @Override
            public void gotoNext(SimpleBug data) {
                TicketKeyService ticketKeyService = AppContextUtil.getSpringBean(TicketKeyService.class);
                Integer nextId = ticketKeyService.getNextKey(data.getProjectid(), data.getTicketKey());
                if (nextId != null) {
                    EventBusFactory.getInstance().post(new TicketEvent.GotoRead(this, data.getProjectid(), nextId));
                } else {
                    NotificationUtil.showGotoLastRecordNotification();
                }
            }

            @Override
            public void gotoPrevious(SimpleBug data) {
                TicketKeyService ticketKeyService = AppContextUtil.getSpringBean(TicketKeyService.class);
                Integer previousId = ticketKeyService.getPreviousKey(data.getProjectid(), data.getTicketKey());
                if (previousId != null) {
                    EventBusFactory.getInstance().post(new TicketEvent.GotoRead(this, data.getProjectid(), previousId));
                } else {
                    NotificationUtil.showGotoLastRecordNotification();
                }
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new TicketEvent.GotoDashboard(this, null));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.BUGS)) {
            SimpleBug bug = null;
            if (data.getParams() instanceof Integer) {
                BugService bugService = AppContextUtil.getSpringBean(BugService.class);
                bug = bugService.findById((Integer) data.getParams(), AppUI.getAccountId());
            } else if (data.getParams() instanceof SimpleBug) {
                bug = (SimpleBug) data.getParams();
            }
            if (bug != null) {
                ProjectView projectView = (ProjectView) container;
                projectView.gotoSubView(ProjectView.TICKET_ENTRY, view);
                view.previewItem(bug);

                ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
                breadcrumb.gotoBugRead(bug);
            } else {
                throw new ResourceNotFoundException();
            }
        } else {
            throw new SecureAccessException();
        }
    }
}
