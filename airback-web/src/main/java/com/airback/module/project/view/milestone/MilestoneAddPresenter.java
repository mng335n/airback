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
import com.airback.configuration.SiteConfiguration;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.file.AttachmentUtils;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.Milestone;
import com.airback.module.project.domain.SimpleMilestone;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.event.MilestoneEvent;
import com.airback.module.project.i18n.OptionI18nEnum.MilestoneStatus;
import com.airback.module.project.i18n.ProjectCommonI18nEnum;
import com.airback.module.project.service.MilestoneService;
import com.airback.module.project.service.ProjectTicketService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
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
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.airback.vaadin.web.ui.field.AttachmentUploadField;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 2.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class MilestoneAddPresenter extends AbstractPresenter<MilestoneAddView> {
    private static final long serialVersionUID = 1L;

    public MilestoneAddPresenter() {
        super(MilestoneAddView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new IEditFormHandler<SimpleMilestone>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(SimpleMilestone milestone) {
                int milestoneId = saveMilestone(milestone);
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoRead(this, milestoneId));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoList(this, null));
            }

            @Override
            public void onSaveAndNew(SimpleMilestone milestone) {
                saveMilestone(milestone);
                EventBusFactory.getInstance().post(new MilestoneEvent.GotoAdd(this, null));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.MILESTONES)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.MILESTONE_ENTRY, view);

            SimpleMilestone milestone = (SimpleMilestone) data.getParams();

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            if (milestone.getId() == null) {
                milestone.setSaccountid(AppUI.getAccountId());
                breadcrumb.gotoMilestoneAdd();
            } else {
                breadcrumb.gotoMilestoneEdit(milestone);
            }
            view.editItem(milestone);
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

    private int saveMilestone(Milestone milestone) {
        MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
        milestone.setProjectid(CurrentProjectVariables.getProjectId());
        milestone.setSaccountid(AppUI.getAccountId());

        if (milestone.getId() == null) {
            milestone.setCreateduser(UserUIContext.getUsername());
            milestoneService.saveWithSession(milestone, UserUIContext.getUsername());
        } else {
            milestoneService.updateWithSession(milestone, UserUIContext.getUsername());
        }
        AttachmentUploadField uploadField = view.getAttachUploadField();
        String attachPath = AttachmentUtils.getProjectEntityAttachmentPath(AppUI.getAccountId(), milestone.getProjectid(),
                ProjectTypeConstants.MILESTONE, "" + milestone.getId());
        uploadField.saveContentsToRepo(attachPath);

        if (!SiteConfiguration.isCommunityEdition() && MilestoneStatus.Closed.name().equals(milestone.getStatus())) {
            ProjectTicketSearchCriteria searchCriteria = new ProjectTicketSearchCriteria();
            searchCriteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
            searchCriteria.setTypes(new SetSearchField<>(ProjectTypeConstants.BUG, ProjectTypeConstants.RISK,
                    ProjectTypeConstants.TASK));
            searchCriteria.setMilestoneId(NumberSearchField.equal(milestone.getId()));
            searchCriteria.setOpen(new SearchField());
            ProjectTicketService genericTaskService = AppContextUtil.getSpringBean(ProjectTicketService.class);
            int openAssignmentsCount = genericTaskService.getTotalCount(searchCriteria);
            if (openAssignmentsCount > 0) {
                ConfirmDialogExt.show(UI.getCurrent(),
                        UserUIContext.getMessage(GenericI18Enum.OPT_QUESTION, AppUI.getSiteName()),
                        UserUIContext.getMessage(ProjectCommonI18nEnum.OPT_CLOSE_SUB_ASSIGNMENTS),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                genericTaskService.closeSubAssignmentOfMilestone(milestone.getId());
                            }
                        });
            }
        }
        return milestone.getId();
    }

}
