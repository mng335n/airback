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

import com.airback.common.i18n.GenericI18Enum;
import com.airback.module.user.accountsettings.localization.UserI18nEnum;
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
import com.airback.vaadin.event.DefaultPreviewFormHandler;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_USER, impliedPermissionVal = AccessPermissionFlag.READ_ONLY)
public class UserReadPresenter extends AbstractPresenter<UserReadView> {
    private static final long serialVersionUID = 1L;

    public UserReadPresenter() {
        super(UserReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<User>() {
            @Override
            public void onAdd(User data) {
                EventBusFactory.getInstance().post(new UserEvent.GotoAdd(this, null));
            }

            @Override
            public void onEdit(User data) {
                EventBusFactory.getInstance().post(new UserEvent.GotoEdit(this, data));
            }

            @Override
            public void onDelete(final User data) {
                ConfirmDialogExt.show(UI.getCurrent(),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                UserService userService = AppContextUtil.getSpringBean(UserService.class);
                                userService.pendingUserAccount(data.getUsername(), AppUI.getAccountId());
                                EventBusFactory.getInstance().post(new UserEvent.GotoList(this, null));
                            }
                        });
            }

            @Override
            public void onClone(User data) {
                User cloneData = (User) data.copy();
                cloneData.setUsername(null);
                EventBusFactory.getInstance().post(new UserEvent.GotoAdd(this, cloneData));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new UserEvent.GotoList(this, null));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (UserUIContext.canRead(RolePermissionCollections.ACCOUNT_USER)) {
            String username = (String) data.getParams();

            UserService userService = AppContextUtil.getSpringBean(UserService.class);
            SimpleUser user = userService.findUserByUserNameInAccount(username, AppUI.getAccountId());
            if (user != null) {
                AccountModule accountModule = (AccountModule) container;
                accountModule.gotoSubView(SettingUIConstants.USERS, view);
                view.previewItem(user);

                AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
                breadcrumb.gotoUserRead(user);
            } else {
                NotificationUtil.showErrorNotification(UserUIContext.getMessage(UserI18nEnum.ERROR_NO_USER_IN_ACCOUNT, username));
            }
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
