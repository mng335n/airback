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
package com.airback.vaadin.web.ui;

import com.airback.core.airbackException;
import com.airback.core.ResourceNotFoundException;
import com.airback.core.SecureAccessException;
import com.airback.vaadin.EventBusFactory;
import com.airback.module.user.accountsettings.view.AccountModulePresenter;
import com.airback.security.PermissionChecker;
import com.airback.security.PermissionMap;
import com.airback.shell.event.ShellEvent;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.*;
import com.airback.vaadin.mvp.service.ComponentScannerService;
import com.airback.vaadin.ui.NotificationUtil;
import com.vaadin.ui.HasComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.airback.core.utils.ExceptionUtils.getExceptionType;

/**
 * @param <V>
 * @author airback Ltd.
 * @since 2.0
 */
public abstract class AbstractPresenter<V extends PageView> implements IPresenter<V> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPresenter.class);

    private Class<V> viewClass;
    private Class<V> implClass;
    protected V view;

    public AbstractPresenter(Class<V> viewClass) {
        this.viewClass = viewClass;
        ComponentScannerService componentScannerService = AppContextUtil.getSpringBean(ComponentScannerService.class);
        implClass = (Class<V>) componentScannerService.getViewImplCls(viewClass);
        if (implClass == null) {
            throw new airbackException("Can not find the implementation for view " + viewClass);
        }
    }

    @Override
    public V getView() {
        initView();
        return view;
    }

    private void initView() {
        if (view == null) {
            try {
                view = implClass.newInstance();
                view.addAttachListener(attachEvent -> {
                    if (view instanceof InitializingView) {
                        ((InitializingView) view).initContent();
                    }
                    viewAttached();
                });

                view.addDetachListener(detachEvent -> viewDetached());
                postInitView();
            } catch (Exception e) {
                throw new airbackException("Can not init view " + implClass, e);
            }
        }
    }

    protected void postInitView() {
    }

    protected void viewAttached() {
    }

    protected void viewDetached() {
    }

    @Override
    public boolean go(HasComponents container, ScreenData<?> data) {
        if (!UserUIContext.getInstance().getIsValidAccount() && (!(this instanceof AccountModulePresenter)
                && ModuleHelper.getCurrentModule() != null && !ModuleHelper.isCurrentAccountModule())) {
            EventBusFactory.getInstance().post(new ShellEvent.GotoUserAccountModule(this, new String[]{"billing"}));
            return true;
        } else {
            initView();

            if (view == null) {
                LOG.error("Can not find view " + viewClass);
                return false;
            }

            if (checkPermissionAccessIfAny()) {
                try {
                    onGo(container, data);
                } catch (Throwable e) {
                    onErrorStopChain(e);
                    return false;
                }
            } else {
                NotificationUtil.showMessagePermissionAlert();
            }
            return true;
        }
    }

    protected abstract void onGo(HasComponents container, ScreenData<?> data);

    private boolean checkPermissionAccessIfAny() {
        ViewPermission viewPermission = this.getClass().getAnnotation(ViewPermission.class);
        if (viewPermission != null) {
            String permissionId = viewPermission.permissionId();
            int impliedPermissionVal = viewPermission.impliedPermissionVal();

            if (UserUIContext.isAdmin()) {
                return true;
            } else {
                PermissionMap permissionMap = UserUIContext.getPermissionMap();
                if (permissionMap == null) {
                    return false;
                } else {
                    Integer value = permissionMap.get(permissionId);
                    return (value != null) && PermissionChecker.isImplied(value, impliedPermissionVal);
                }
            }
        } else {
            return true;
        }
    }

    @Override
    public final void handleChain(HasComponents container, PageActionChain pageActionChain) {
        ScreenData pageAction = pageActionChain.pop();
        boolean isSuccess = go(container, pageAction);

        if (isSuccess) {
            if (pageActionChain.hasNext()) {
                onHandleChain(container, pageActionChain);
            } else {
                onDefaultStopChain();
            }
        }
    }

    protected void onDefaultStopChain() {

    }

    protected void onHandleChain(HasComponents container, PageActionChain pageActionChain) {
        throw new UnsupportedOperationException("You need override this method");
    }

    protected void onErrorStopChain(Throwable throwable) {
        if (getExceptionType(throwable, ResourceNotFoundException.class) != null) {
            NotificationUtil.showRecordNotExistNotification();
        } else if (getExceptionType(throwable, SecureAccessException.class) != null) {
            NotificationUtil.showMessagePermissionAlert();
        } else {
            throw new airbackException(throwable);
        }
    }
}
