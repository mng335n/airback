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
package com.airback.module.project.view.user;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.db.arguments.SetSearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.domain.ProjectGenericItem;
import com.airback.module.project.domain.criteria.ProjectGenericItemSearchCriteria;
import com.airback.module.project.i18n.ProjectI18nEnum;
import com.airback.module.project.service.ProjectGenericItemService;
import com.airback.module.project.service.ProjectService;
import com.airback.module.project.ui.components.GenericItemRowDisplayHandler;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.AbstractVerticalPageView;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.DefaultBeanPagedList;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MCssLayout;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 5.0.3
 */
@ViewComponent
public class ProjectSearchItemsViewImpl extends AbstractVerticalPageView implements ProjectSearchItemsView {

    public ProjectSearchItemsViewImpl() {
        this.withMargin(true);
    }

    @Override
    public void displayResults(String value) {
        this.removeAllComponents();

        ELabel headerLbl = ELabel.h2("");

        ProjectService projectService = AppContextUtil.getSpringBean(ProjectService.class);
        List<Integer> projectKeys = projectService.getOpenProjectKeysUserInvolved(UserUIContext.getUsername(), AppUI.getAccountId());
        if (projectKeys.size() > 0) {
            ProjectGenericItemSearchCriteria criteria = new ProjectGenericItemSearchCriteria();
            criteria.setPrjKeys(new SetSearchField<>(projectKeys));
            criteria.setTxtValue(StringSearchField.and(value));
            DefaultBeanPagedList<ProjectGenericItemService, ProjectGenericItemSearchCriteria, ProjectGenericItem>
                    searchItemsTable = new DefaultBeanPagedList<>(AppContextUtil.getSpringBean(ProjectGenericItemService.class),
                    new GenericItemRowDisplayHandler());
            int foundNum = searchItemsTable.setSearchCriteria(criteria);
            headerLbl.setValue(String.format(VaadinIcons.SEARCH.getHtml() + " " + UserUIContext.getMessage(ProjectI18nEnum.OPT_SEARCH_TERM)
                    , value, foundNum));

            this.with(headerLbl, searchItemsTable).expand(searchItemsTable);
        } else {
            this.with(new MCssLayout(new Label(UserUIContext.getMessage(GenericI18Enum.VIEW_NO_ITEM_TITLE))).withFullWidth());
        }
    }
}
