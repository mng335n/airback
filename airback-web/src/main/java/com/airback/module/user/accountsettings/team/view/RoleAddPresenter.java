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

import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.domain.Role;
import com.airback.module.user.event.RoleEvent;
import com.airback.module.user.service.RoleService;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.IEditFormHandler;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_ROLE, impliedPermissionVal = AccessPermissionFlag.READ_WRITE)
public class RoleAddPresenter extends AbstractPresenter<RoleAddView> {
    private static final long serialVersionUID = 1L;

    public RoleAddPresenter() {
        super(RoleAddView.class);
    }

    @Override
    protected void postInitView() {
        view.getEditFormHandlers().addFormHandler(new IEditFormHandler<Role>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSave(Role item) {
                save(item);
                EventBusFactory.getInstance().post(new RoleEvent.GotoList(this, null));
            }

            @Override
            public void onCancel() {
                EventBusFactory.getInstance().post(new RoleEvent.GotoList(this, null));
            }

            @Override
            public void onSaveAndNew(Role item) {
                save(item);
                EventBusFactory.getInstance().post(new RoleEvent.GotoAdd(this, null));
            }
        });
    }

    public void save(Role item) {
        RoleService roleService = AppContextUtil.getSpringBean(RoleService.class);
        item.setSaccountid(AppUI.getAccountId());

        if (item.getId() == null) {
            roleService.saveWithSession(item, UserUIContext.getUsername());
        } else {
            roleService.updateWithSession(item, UserUIContext.getUsername());
        }

        roleService.savePermission(item.getId(), view.getPermissionMap(), item.getSaccountid());
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (UserUIContext.canWrite(RolePermissionCollections.ACCOUNT_ROLE)) {
            AccountModule accountModule = (AccountModule) container;
            accountModule.gotoSubView(SettingUIConstants.ROLES, view);

            Role role = (Role) data.getParams();
            AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);

            if (role.getId() == null) {
                role.setSaccountid(AppUI.getAccountId());
                breadcrumb.gotoRoleAdd();
            } else {
                breadcrumb.gotoRoleEdit(role);
            }
            view.editItem(role);
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }
}
