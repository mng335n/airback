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
package com.airback.module.project.view;

import com.airback.common.i18n.OptionI18nEnum;
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.db.arguments.SetSearchField;
import com.airback.db.persistence.service.ISearchableService;
import com.airback.module.project.domain.SimpleProject;
import com.airback.module.project.domain.criteria.ProjectSearchCriteria;
import com.airback.module.project.i18n.ProjectCommonI18nEnum;
import com.airback.module.project.i18n.ProjectI18nEnum;
import com.airback.module.project.service.ProjectService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.web.ui.DefaultMassEditActionHandler;
import com.airback.vaadin.web.ui.ListSelectionPresenter;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HasComponents;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

/**
 * @author airback Ltd
 * @since 5.2.12
 */
public class ProjectListPresenter extends ListSelectionPresenter<ProjectListView, ProjectSearchCriteria, SimpleProject> {

    private ProjectService projectService;

    public ProjectListPresenter() {
        super(ProjectListView.class);
        projectService = AppContextUtil.getSpringBean(ProjectService.class);
    }

    @Override
    protected void viewAttached() {
        super.viewAttached();

        view.getPopupActionHandlers().setMassActionHandler(new DefaultMassEditActionHandler(this) {

            @Override
            protected String getReportTitle() {
                return UserUIContext.getMessage(ProjectI18nEnum.LIST);
            }

            @Override
            protected Class<?> getReportModelClassType() {
                return SimpleProject.class;
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        BoardContainer boardContainer = (BoardContainer) container;
        boardContainer.gotoSubView("Projects", view);

        ProjectSearchCriteria searchCriteria = new ProjectSearchCriteria();
        searchCriteria.setStatuses(new SetSearchField<>(StatusI18nEnum.Open.name()));
        doSearch(searchCriteria);

        AppUI.addFragment("project/list", UserUIContext.getMessage(ProjectI18nEnum.LIST));
    }

    @Override
    public void doSearch(ProjectSearchCriteria searchCriteria) {
        Collection<Integer> prjKeys;
        if (UserUIContext.isAdmin()) {
            prjKeys = projectService.getProjectKeysUserInvolved(null, AppUI.getAccountId());
        } else {
            prjKeys = projectService.getProjectKeysUserInvolved(UserUIContext.getUsername(), AppUI.getAccountId());
        }

        if (CollectionUtils.isNotEmpty(prjKeys)) {
            searchCriteria.setProjectKeys(new SetSearchField<>(prjKeys));
            super.doSearch(searchCriteria);
        }
    }

    @Override
    public ISearchableService<ProjectSearchCriteria> getSearchService() {
        return projectService;
    }

    @Override
    protected void deleteSelectedItems() {
        throw new UnsupportedOperationException("Not supported");
    }
}
