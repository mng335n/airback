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

import com.airback.core.SecureAccessException;
import com.airback.db.persistence.service.ISearchableService;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.ProjectRole;
import com.airback.module.project.domain.SimpleProjectRole;
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria;
import com.airback.module.project.i18n.ProjectMemberI18nEnum;
import com.airback.module.project.i18n.ProjectRoleI18nEnum;
import com.airback.module.project.service.ProjectRoleService;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectView;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.ViewItemAction;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.DefaultMassEditActionHandler;
import com.airback.vaadin.web.ui.ListSelectionPresenter;
import com.airback.vaadin.web.ui.MailFormWindow;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectRoleListPresenter extends ListSelectionPresenter<ProjectRoleListView, ProjectRoleSearchCriteria, SimpleProjectRole> {
    private static final long serialVersionUID = 1L;
    private ProjectRoleService projectRoleService;

    public ProjectRoleListPresenter() {
        super(ProjectRoleListView.class);
    }

    @Override
    protected void postInitView() {
        super.postInitView();
        projectRoleService = AppContextUtil.getSpringBean(ProjectRoleService.class);

        view.getPopupActionHandlers().setMassActionHandler(new DefaultMassEditActionHandler(this) {

            @Override
            protected void onSelectExtra(String id) {
                if (ViewItemAction.MAIL_ACTION.equals(id)) {
                    UI.getCurrent().addWindow(new MailFormWindow());
                }
            }

            @Override
            protected String getReportTitle() {
                return UserUIContext.getMessage(ProjectRoleI18nEnum.LIST);
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            protected Class getReportModelClassType() {
                return SimpleProjectRole.class;
            }
        });
    }

    @Override
    protected void deleteSelectedItems() {
        if (!isSelectAll) {
            Collection<SimpleProjectRole> roles = view.getPagedBeanGrid().getItems();
            List<ProjectRole> keyList = new ArrayList<>();
            for (ProjectRole item : roles) {
                if (item.isSelected()) {
                    if (Boolean.TRUE.equals(item.getIssystemrole())) {
                        NotificationUtil.showErrorNotification(UserUIContext.
                                getMessage(ProjectMemberI18nEnum.CAN_NOT_DELETE_ROLE_MESSAGE, item.getRolename()));
                    } else {
                        keyList.add(item);
                    }
                }
            }

            if (keyList.size() > 0) {
                projectRoleService.massRemoveWithSession(keyList, UserUIContext.getUsername(), AppUI.getAccountId());
                doSearch(searchCriteria);
                checkWhetherEnableTableActionControl();
            }
        } else {
            projectRoleService.removeByCriteria(searchCriteria, AppUI.getAccountId());
            doSearch(searchCriteria);
        }

    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.ROLES)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.ROLE_ENTRY, view);
            searchCriteria = (ProjectRoleSearchCriteria) data.getParams();
            doSearch(searchCriteria);

            ProjectBreadcrumb breadCrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadCrumb.gotoRoleList();
        } else {
            throw new SecureAccessException();
        }
    }

    @Override
    public ISearchableService<ProjectRoleSearchCriteria> getSearchService() {
        return AppContextUtil.getSpringBean(ProjectRoleService.class);
    }
}
