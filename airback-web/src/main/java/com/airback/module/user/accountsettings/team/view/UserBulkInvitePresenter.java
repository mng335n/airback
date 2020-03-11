package com.airback.module.user.accountsettings.team.view;

import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd
 * @since 7.0.2
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_USER, impliedPermissionVal = AccessPermissionFlag.READ_WRITE)
public class UserBulkInvitePresenter extends AbstractPresenter<UserBulkInviteView> {
    private static final long serialVersionUID = 1L;

    public UserBulkInvitePresenter() {
        super(UserBulkInviteView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        AccountModule accountModule = (AccountModule) container;
        accountModule.gotoSubView(SettingUIConstants.USERS, view);

        AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
        breadcrumb.gotoUserBulkInvite();
        view.display();
    }
}
