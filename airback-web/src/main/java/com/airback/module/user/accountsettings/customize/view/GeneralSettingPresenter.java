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
package com.airback.module.user.accountsettings.customize.view;

import com.airback.module.user.accountsettings.localization.AdminI18nEnum;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.BooleanPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 5.1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_THEME, impliedPermissionVal = BooleanPermissionFlag.TRUE)
public class GeneralSettingPresenter extends AbstractPresenter<GeneralSettingView> {
    public GeneralSettingPresenter() {
        super(GeneralSettingView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (UserUIContext.isAdmin()) {
            AccountModule customizeContainer = (AccountModule) container;
            customizeContainer.gotoSubView(SettingUIConstants.GENERAL_SETTING, view);
            view.displayView();
            AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
            breadcrumb.gotoGeneralSetting();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
