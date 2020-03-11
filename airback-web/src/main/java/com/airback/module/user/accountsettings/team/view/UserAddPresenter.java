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

import com.airback.core.utils.RandomPasswordGenerator;
import com.airback.core.utils.StringUtils;
import com.airback.module.billing.RegisterStatusConstants;
import com.airback.module.billing.UserStatusConstants;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.domain.SimpleUser;
import com.airback.module.user.domain.User;
import com.airback.module.user.event.UserEvent;
import com.airback.module.user.service.UserService;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.DefaultEditFormHandler;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.ui.UIUtils;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_USER, impliedPermissionVal = AccessPermissionFlag.READ_WRITE)
public class UserAddPresenter extends AbstractPresenter<UserAddView> {
    private static final long serialVersionUID = 1L;

    public UserAddPresenter() {
        super(UserAddView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new DefaultEditFormHandler<SimpleUser>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(SimpleUser item) {
                save(item);
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new UserEvent.GotoList(this, null));
            }
        });
    }

    private void save(SimpleUser user) {
        boolean isRefreshable = false;
        if (user.getUsername() != null && user.getUsername().equals(UserUIContext.getUsername())) {
            isRefreshable = true;
        }

        UserService userService = AppContextUtil.getSpringBean(UserService.class);
        user.setAccountId(AppUI.getAccountId());
        user.setSubDomain(AppUI.getSubDomain());

        if (StringUtils.isBlank(user.getStatus())) {
            user.setStatus(UserStatusConstants.EMAIL_VERIFIED_REQUEST);
        }

        if (StringUtils.isBlank(user.getRegisterstatus())) {
            user.setRegisterstatus(RegisterStatusConstants.NOT_LOG_IN_YET);
        }

        User existingUser = userService.findUserByUserName(user.getUsername());
        if (existingUser == null) {
            if (StringUtils.isBlank(user.getPassword())) {
                user.setPassword(RandomPasswordGenerator.generateRandomPassword());
            }
            String userPassword = user.getPassword();
            userService.saveUserAccount(user, user.getRoleId(), AppUI.getSubDomain(), AppUI.getAccountId(), UserUIContext.getUsername(), true);
            UI.getCurrent().addWindow(new NewUserAddedWindow(user, userPassword));
        } else {
            userService.updateUserAccount(user, AppUI.getAccountId());
            EventBusFactory.getInstance().post(new UserEvent.GotoList(this, null));
        }

        if (isRefreshable) {
            UIUtils.reloadPage();
        }
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        AccountModule accountModule = (AccountModule) container;
        accountModule.gotoSubView(SettingUIConstants.USERS, view);

        SimpleUser user = (SimpleUser) data.getParams();

        AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);

        if (user.getUsername() == null) {
            user.setAccountId(AppUI.getAccountId());
            view.editItem(user, false);
            breadcrumb.gotoUserAdd();
        } else {
            breadcrumb.gotoUserEdit(user);
            view.editItem(user);
        }
    }
}