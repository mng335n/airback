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
import com.airback.module.user.accountsettings.localization.RoleI18nEnum;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.domain.Role;
import com.airback.module.user.domain.SimpleRole;
import com.airback.module.user.event.RoleEvent;
import com.airback.module.user.service.RoleService;
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
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_ROLE, impliedPermissionVal = AccessPermissionFlag.READ_ONLY)
public class RoleReadPresenter extends AbstractPresenter<RoleReadView> {
    private static final long serialVersionUID = 1L;

    public RoleReadPresenter() {
        super(RoleReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<Role>() {
            @Override
            public void onAdd(Role role) {
                EventBusFactory.getInstance().post(new RoleEvent.GotoAdd(this, role));
            }

            @Override
            public void onEdit(Role data) {
                EventBusFactory.getInstance().post(new RoleEvent.GotoEdit(this, data));
            }

            @Override
            public void onDelete(final Role role) {
                if (Boolean.TRUE.equals(role.getIssystemrole()) || Boolean.TRUE.equals(role.getIsdefault())) {
                    NotificationUtil.showErrorNotification(UserUIContext.getMessage(RoleI18nEnum.ERROR_CAN_NOT_DELETE_SYSTEM_ROLE, role.getRolename()));
                } else {
                    ConfirmDialogExt.show(UI.getCurrent(),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                            confirmDialog -> {
                                if (confirmDialog.isConfirmed()) {
                                    RoleService roleService = AppContextUtil.getSpringBean(RoleService.class);
                                    roleService.removeWithSession(role, UserUIContext.getUsername(), AppUI.getAccountId());
                                    EventBusFactory.getInstance().post(new RoleEvent.GotoList(this, null));
                                }
                            });
                }
            }

            @Override
            public void onClone(Role data) {
                Role cloneData = (Role) data.copy();
                cloneData.setRolename(null);
                cloneData.setIssystemrole(false);
                EventBusFactory.getInstance().post(new RoleEvent.GotoAdd(this, cloneData));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new RoleEvent.GotoList(this, null));
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (UserUIContext.canRead(RolePermissionCollections.ACCOUNT_ROLE)) {
            RoleService roleService = AppContextUtil.getSpringBean(RoleService.class);
            SimpleRole role = roleService.findById((Integer) data.getParams(), AppUI.getAccountId());
            if (role != null) {
                AccountModule accountModule = (AccountModule) container;
                accountModule.gotoSubView(SettingUIConstants.ROLES, view);
                view.previewItem(role);

                AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
                breadcrumb.gotoRoleRead(role);
            } else {
                NotificationUtil.showRecordNotExistNotification();
            }
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
