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
package com.airback.module.user.accountsettings.team.view;

import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.billing.RegisterStatusConstants;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.domain.criteria.UserSearchCriteria;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_USER, impliedPermissionVal = AccessPermissionFlag.READ_ONLY)
public class UserListPresenter extends AbstractPresenter<UserListView> {
    private static final long serialVersionUID = 1L;

    public UserListPresenter() {
        super(UserListView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        AccountModule accountModule = (AccountModule) container;
        accountModule.gotoSubView(SettingUIConstants.USERS, view);

        UserSearchCriteria criteria;
        if (data == null) {
            criteria = new UserSearchCriteria();
            criteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
            criteria.setRegisterStatuses(new SetSearchField<>(RegisterStatusConstants.ACTIVE,
                    RegisterStatusConstants.NOT_LOG_IN_YET));
        } else {
            criteria = (UserSearchCriteria) data.getParams();
        }

        view.setSearchCriteria(criteria);

        AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
        breadcrumb.gotoUserList();
    }
}
