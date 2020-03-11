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
import com.airback.module.project.i18n.VersionI18nEnum;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericListPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.module.project.domain.SimpleVersion;
import com.airback.module.project.domain.Version;
import com.airback.module.project.domain.criteria.VersionSearchCriteria;
import com.airback.module.project.service.VersionService;
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
public class VersionListPresenter extends ProjectGenericListPresenter<VersionListView, VersionSearchCriteria, SimpleVersion> {
    private static final long serialVersionUID = 1L;
    private final VersionService versionService;

    public VersionListPresenter() {
        super(VersionListView.class);
        versionService = AppContextUtil.getSpringBean(VersionService.class);
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
                return UserUIContext.getMessage(VersionI18nEnum.LIST);
            }

            @Override
            protected Class<?> getReportModelClassType() {
                return SimpleVersion.class;
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.VERSIONS)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.VERSION_ENTRY, view);

            searchCriteria = (VersionSearchCriteria) data.getParams();
            int totalCount = versionService.getTotalCount(searchCriteria);

            if (totalCount > 0) {
                doSearch(searchCriteria);
            } else {
                view.showNoItemView();
            }

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoVersionList();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

    @Override
    protected void deleteSelectedItems() {
        if (!isSelectAll) {
            Collection<SimpleVersion> currentDataList = view.getPagedBeanGrid().getItems();
            List<Version> keyList = new ArrayList<>();
            for (Version item : currentDataList) {
                if (item.isSelected()) {
                    keyList.add(item);
                }
            }

            if (keyList.size() > 0) {
                versionService.massRemoveWithSession(keyList, UserUIContext.getUsername(), AppUI.getAccountId());
            }
        } else {
            versionService.removeByCriteria(searchCriteria, AppUI.getAccountId());
        }

        int totalCount = versionService.getTotalCount(searchCriteria);

        if (totalCount > 0) {
            doSearch(searchCriteria);
        } else {
            view.showNoItemView();
        }

    }

    @Override
    public ISearchableService<VersionSearchCriteria> getSearchService() {
        return versionService;
    }
}
