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
package com.airback.shell.view;

import com.airback.common.i18n.ShellI18nEnum;
import com.airback.module.mail.service.ExtMailService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.AbstractPresenter;
import com.vaadin.ui.HasComponents;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ForgotPasswordPresenter extends AbstractPresenter<ForgotPasswordView> {
    private static final long serialVersionUID = 1L;

    public ForgotPasswordPresenter() {
        super(ForgotPasswordView.class);
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        MainWindowContainer windowContainer = (MainWindowContainer) container;
        windowContainer.setContent(view);

        ExtMailService extMailService = AppContextUtil.getSpringBean(ExtMailService.class);
        if (!extMailService.isMailSetupValid()) {
            NotificationUtil.showErrorNotification(UserUIContext.getMessage(ShellI18nEnum.WINDOW_SMTP_CONFIRM_SETUP_FOR_USER));
        }

        AppUI.addFragment("user/forgotpassword", UserUIContext.getMessage(ShellI18nEnum.OPT_FORGOT_PASSWORD_VIEW_TITLE));
    }
}
