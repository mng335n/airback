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
package com.airback.module.project.view.page;

import com.airback.core.SecureAccessException;
import com.airback.module.page.domain.PageResource;
import com.airback.module.page.service.PageService;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.vaadin.ui.HasComponents;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 4.4.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class PageListPresenter extends ProjectGenericPresenter<PageListView> {
    private static final long serialVersionUID = 1L;

    private PageService pageService;

    public PageListPresenter() {
        super(PageListView.class);
        pageService = AppContextUtil.getSpringBean(PageService.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.PAGES)) {
            ProjectView pageContainer = (ProjectView) container;
            pageContainer.gotoSubView(ProjectView.PAGE_ENTRY, view);

            String path = (String) data.getParams();
            if (path == null) {
                path = CurrentProjectVariables.getCurrentPagePath();
            } else {
                CurrentProjectVariables.setCurrentPagePath(path);
            }
            List<PageResource> resources = pageService.getResources(path, UserUIContext.getUsername());
            if (!CollectionUtils.isEmpty(resources)) {
                view.displayDefaultPages(resources);
            } else {
                view.showNoItemView();
            }

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoPageList();
        } else {
            throw new SecureAccessException();
        }
    }

}
