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
package com.airback.module.user.accountsettings.profile.view;

import com.airback.core.airbackException;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.vaadin.mvp.PresenterResolver;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProfilePresenter extends AbstractPresenter<ProfileContainer> {
    private static final long serialVersionUID = 1L;

    public ProfilePresenter() {
        super(ProfileContainer.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        AccountModule accountContainer = (AccountModule) container;
        accountContainer.gotoSubView(SettingUIConstants.PROFILE, view);

        AbstractPresenter<?> presenter;
        if (data == null) {
            presenter = PresenterResolver.getPresenter(ProfileReadPresenter.class);
        } else {
            throw new airbackException("Do not support screen data");
        }

        presenter.go(view, data);
    }

}
