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
package com.airback.module.project.view;

import com.airback.module.project.i18n.ProjectCommonI18nEnum;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewScope;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 5.2.4
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class UserProjectDashboardPresenter extends AbstractPresenter<UserProjectDashboardView> {
    public UserProjectDashboardPresenter() {
        super(UserProjectDashboardView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData data) {
        BoardContainer boardContainer = (BoardContainer) container;
        boardContainer.gotoSubView("Dashboard", view);
        view.lazyLoadView();

        AppUI.addFragment("project/dashboard", UserUIContext.getMessage(ProjectCommonI18nEnum.VIEW_DASHBOARD));
    }
}
