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
package com.airback.module.project.view.task;

import com.airback.common.domain.MonitorItem;
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.common.service.MonitorItemService;
import com.airback.core.SecureAccessException;
import com.airback.module.file.AttachmentUtils;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.SimpleTask;
import com.airback.module.project.domain.Task;
import com.airback.module.project.event.TaskEvent;
import com.airback.module.project.event.TicketEvent;
import com.airback.module.project.service.TaskService;
import com.airback.module.project.service.TicketRelationService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
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
import com.airback.vaadin.web.ui.field.AttachmentUploadField;
import com.vaadin.ui.HasComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class TaskAddPresenter extends ProjectGenericPresenter<TaskAddView> {
    private static final long serialVersionUID = 1L;

    public TaskAddPresenter() {
        super(TaskAddView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new IEditFormHandler<SimpleTask>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(SimpleTask item) {
                int taskId = save(item);
                EventBusFactory.getInstance().post(new TaskEvent.GotoRead(this, taskId));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new TicketEvent.GotoDashboard(this, null));
            }

            @Override
            public void onSaveAndNew(final SimpleTask item) {
                save(item);
                EventBusFactory.getInstance().post(new TaskEvent.GotoAdd(this, null));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.TASKS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.TICKET_ENTRY, view);
            SimpleTask task = (SimpleTask) data.getParams();

            ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            if (task.getId() == null) {
                breadCrumb.gotoTaskAdd();
                task.setSaccountid(AppUI.getAccountId());
            } else {
                breadCrumb.gotoTaskEdit(task);
            }
            view.editItem(task);
        } else {
            throw new SecureAccessException();
        }
    }

    private int save(Task item) {
        TaskService taskService = AppContextUtil.getSpringBean(TaskService.class);

        item.setSaccountid(AppUI.getAccountId());
        item.setProjectid(CurrentProjectVariables.getProjectId());
        if (item.getPercentagecomplete() == null) {
            item.setPercentagecomplete(0d);
        } else if (item.getPercentagecomplete() == 100d) {
            item.setStatus(StatusI18nEnum.Closed.name());
        }

        if (item.getStatus() == null) {
            item.setStatus(StatusI18nEnum.Open.name());
        }

        if (item.getId() == null) {
            item.setCreateduser(UserUIContext.getUsername());
            int taskId = taskService.saveWithSession(item, UserUIContext.getUsername());

            TicketRelationService ticketRelationService = AppContextUtil.getSpringBean(TicketRelationService.class);
            ticketRelationService.saveComponentsOfTicket(taskId, ProjectTypeConstants.TASK, view.getComponents());
            ticketRelationService.saveAffectedVersionsOfTicket(taskId, ProjectTypeConstants.TASK, view.getAffectedVersions());

            List<String> followers = view.getFollowers();
            if (followers.size() > 0) {
                List<MonitorItem> monitorItems = new ArrayList<>();
                for (String follower : followers) {
                    MonitorItem monitorItem = new MonitorItem();
                    monitorItem.setSaccountid(AppUI.getAccountId());
                    monitorItem.setType(ProjectTypeConstants.TASK);
                    monitorItem.setTypeid(taskId + "");
                    monitorItem.setUsername(follower);
                    monitorItem.setExtratypeid(CurrentProjectVariables.getProjectId());
                    monitorItems.add(monitorItem);
                }
                MonitorItemService monitorItemService = AppContextUtil.getSpringBean(MonitorItemService.class);
                monitorItemService.saveMonitorItems(monitorItems);
            }
        } else {
            taskService.updateWithSession(item, UserUIContext.getUsername());
            TicketRelationService ticketRelationService = AppContextUtil.getSpringBean(TicketRelationService.class);
            ticketRelationService.updateComponentsOfTicket(item.getId(), ProjectTypeConstants.TASK, view.getComponents());
            ticketRelationService.updateAffectedVersionsOfTicket(item.getId(), ProjectTypeConstants.TASK, view.getAffectedVersions());
        }
        AttachmentUploadField uploadField = view.getAttachUploadField();
        String attachPath = AttachmentUtils.getProjectEntityAttachmentPath(AppUI.getAccountId(), item.getProjectid(),
                ProjectTypeConstants.TASK, "" + item.getId());
        uploadField.saveContentsToRepo(attachPath);
        return item.getId();
    }
}
