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

import com.airback.common.ModuleNameConstants;
import com.airback.shell.view.MainView;
import com.airback.shell.view.ShellUrlResolver;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.PresenterResolver;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectModulePresenter extends AbstractPresenter<ProjectModule> {
    private static final long serialVersionUID = 1L;

    public ProjectModulePresenter() {
        super(ProjectModule.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        MainView mainView = (MainView) container;
        mainView.addModule(view);

        String[] params = (String[]) data.getParams();
        if (params == null || params.length == 0) {
            BoardContainerPresenter dashboardPresenter = PresenterResolver.getPresenter(BoardContainerPresenter.class);
            dashboardPresenter.go(view, null);
        } else {
            ShellUrlResolver.ROOT.getSubResolver("project").handle(params);
        }

        UserUIContext.updateLastModuleVisit(ModuleNameConstants.PRJ);
    }
}
