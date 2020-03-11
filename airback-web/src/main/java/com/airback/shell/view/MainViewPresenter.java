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
package com.airback.shell.view;

import com.airback.common.ModuleNameConstants;
import com.airback.vaadin.EventBusFactory;
import com.airback.module.user.domain.SimpleUser;
import com.airback.shell.event.ShellEvent;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.airback.web.DesktopApplication;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import org.apache.commons.lang3.StringUtils;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class MainViewPresenter extends AbstractPresenter<MainView> {
    private static final long serialVersionUID = 1L;

    public MainViewPresenter() {
        super(MainView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        // if user type remember URL, instead of going to main page, to to his url
        String url = ((DesktopApplication) UI.getCurrent()).getCurrentFragmentUrl();
        view.display();
        if (!UserUIContext.getInstance().getIsValidAccount()) {
            EventBusFactory.getInstance().post(new ShellEvent.GotoUserAccountModule(this, new String[]{"billing"}));
        } else {
            if (!StringUtils.isBlank(url)) {
                if (url.startsWith("/")) {
                    url = url.substring(1);
                }
                ShellUrlResolver.ROOT.resolveFragment(url);
            } else {
                SimpleUser pref = UserUIContext.getUser();
                if (ModuleNameConstants.ACCOUNT.equals(pref.getLastModuleVisit())) {
                    EventBusFactory.getInstance().post(new ShellEvent.GotoUserAccountModule(this, null));
                } else {
                    EventBusFactory.getInstance().post(new ShellEvent.GotoProjectModule(this, null));
                }
            }
        }
    }
}
