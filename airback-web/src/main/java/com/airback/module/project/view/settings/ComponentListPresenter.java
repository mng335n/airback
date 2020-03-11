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

import com.airback.db.persistence.service.ISearchableService;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.i18n.ComponentI18nEnum;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericListPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.module.project.domain.Component;
import com.airback.module.project.domain.SimpleComponent;
import com.airback.module.project.domain.criteria.ComponentSearchCriteria;
import com.airback.module.project.service.ComponentService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.ViewItemAction;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.DefaultMassEditActionHandler;
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
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class ComponentListPresenter extends ProjectGenericListPresenter<ComponentListView, ComponentSearchCriteria, SimpleComponent> {
    private static final long serialVersionUID = 1L;
    private ComponentService componentService;

    public ComponentListPresenter() {
        super(ComponentListView.class);
        componentService = AppContextUtil.getSpringBean(ComponentService.class);
    }

    @Override
    protected void postInitView() {
        super.postInitView();

        view.getPopupActionHandlers().setMassActionHandler(new DefaultMassEditActionHandler(this) {
            @Override
            protected void onSelectExtra(String id) {
                if (ViewItemAction.MAIL_ACTION.equals(id)) {
                    UI.getCurrent().addWindow(new MailFormWindow());
                }
            }

            @Override
            protected String getReportTitle() {
                return UserUIContext.getMessage(ComponentI18nEnum.LIST);
            }

            @Override
            protected Class<?> getReportModelClassType() {
                return SimpleComponent.class;
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.COMPONENTS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.COMPONENT_ENTRY, view);

            searchCriteria = (ComponentSearchCriteria) data.getParams();
            int totalCount = componentService.getTotalCount(searchCriteria);

            if (totalCount > 0) {
                doSearch(searchCriteria);
            } else {
                view.showNoItemView();
            }

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoComponentList();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

    @Override
    protected void deleteSelectedItems() {
        if (!isSelectAll) {
            Collection<SimpleComponent> currentDataList = view.getPagedBeanGrid().getItems();
            List<Component> keyList = new ArrayList<>();
            for (SimpleComponent item : currentDataList) {
                if (item.isSelected()) {
                    keyList.add(item);
                }
            }

            if (keyList.size() > 0) {
                componentService.massRemoveWithSession(keyList, UserUIContext.getUsername(), AppUI.getAccountId());
            }
        } else {
            componentService.removeByCriteria(searchCriteria, AppUI.getAccountId());
        }

        int totalCount = componentService.getTotalCount(searchCriteria);

        if (totalCount > 0) {
            doSearch(searchCriteria);
        } else {
            view.showNoItemView();
        }

    }

    @Override
    public ISearchableService<ComponentSearchCriteria> getSearchService() {
        return componentService;
    }
}
