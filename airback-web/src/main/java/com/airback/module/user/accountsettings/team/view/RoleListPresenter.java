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
package com.airback.module.user.accountsettings.team.view;

import com.airback.db.persistence.service.ISearchableService;
import com.airback.module.user.accountsettings.localization.RoleI18nEnum;
import com.airback.module.user.accountsettings.view.AccountModule;
import com.airback.module.user.accountsettings.view.AccountSettingBreadcrumb;
import com.airback.module.user.domain.Role;
import com.airback.module.user.domain.SimpleRole;
import com.airback.module.user.domain.criteria.RoleSearchCriteria;
import com.airback.module.user.service.RoleService;
import com.airback.module.user.ui.SettingUIConstants;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.RolePermissionCollections;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.ViewItemAction;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewPermission;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.DefaultMassEditActionHandler;
import com.airback.vaadin.web.ui.ListSelectionPresenter;
import com.airback.vaadin.web.ui.MailFormWindow;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewPermission(permissionId = RolePermissionCollections.ACCOUNT_ROLE, impliedPermissionVal = AccessPermissionFlag.READ_ONLY)
public class RoleListPresenter extends ListSelectionPresenter<RoleListView, RoleSearchCriteria, SimpleRole> {
    private static final long serialVersionUID = 1L;

    private RoleService roleService;

    public RoleListPresenter() {
        super(RoleListView.class);
        roleService = AppContextUtil.getSpringBean(RoleService.class);
    }

    @Override
    protected void postInitView() {
        super.postInitView();

        view.getPopupActionHandlers().setMassActionHandler(new DefaultMassEditActionHandler(this) {
            @Override
            protected void onSelectExtra(String id) {
                if (ViewItemAction.MAIL_ACTION.equals(id)) {
                    UI.getCurrent().addWindow(new MailFormWindow());
                }
            }

            @Override
            protected String getReportTitle() {
                return UserUIContext.getMessage(RoleI18nEnum.LIST);
            }

            @Override
            protected Class<?> getReportModelClassType() {
                return SimpleRole.class;
            }
        });
    }

    @Override
    protected void deleteSelectedItems() {
        if (!isSelectAll) {
            Collection<SimpleRole> currentDataList = view.getPagedBeanGrid().getItems();
            List<Role> keyList = new ArrayList<>();
            for (SimpleRole item : currentDataList) {
                if (item.isSelected()) {
                    if (Boolean.TRUE.equals(item.getIssystemrole())) {
                        NotificationUtil.showErrorNotification(UserUIContext.getMessage(RoleI18nEnum.ERROR_CAN_NOT_DELETE_SYSTEM_ROLE,
                                item.getRolename()));
                    } else {
                        keyList.add(item);
                    }
                }
            }

            if (keyList.size() > 0) {
                roleService.massRemoveWithSession(keyList, UserUIContext.getUsername(), AppUI.getAccountId());
                doSearch(searchCriteria);
            }
        } else {
            roleService.removeByCriteria(searchCriteria, AppUI.getAccountId());
            doSearch(searchCriteria);
        }
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (UserUIContext.canRead(RolePermissionCollections.ACCOUNT_ROLE)) {
            AccountModule accountModule = (AccountModule) container;
            accountModule.gotoSubView(SettingUIConstants.ROLES, view);

            searchCriteria = (RoleSearchCriteria) data.getParams();
            doSearch(searchCriteria);

            AccountSettingBreadcrumb breadcrumb = ViewManager.getCacheComponent(AccountSettingBreadcrumb.class);
            breadcrumb.gotoRoleList();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

    @Override
    public ISearchableService<RoleSearchCriteria> getSearchService() {
        return AppContextUtil.getSpringBean(RoleService.class);
    }
}
