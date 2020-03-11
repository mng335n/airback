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
package com.airback.shell.view

import com.airback.common.i18n.GenericI18Enum
import com.airback.common.i18n.ShellI18nEnum
import com.airback.configuration.SiteConfiguration
import com.airback.vaadin.EventBusFactory
import com.airback.module.mail.service.ExtMailService
import com.airback.shell.event.ShellEvent
import com.airback.spring.AppContextUtil
import com.airback.vaadin.UserUIContext
import com.airback.vaadin.ui.NotificationUtil
import com.airback.vaadin.web.ui.ConfirmDialogExt
import com.vaadin.server.Page
import com.vaadin.ui.UI

/**
 * @author airback Ltd.
 * @since 5.0.4
 */
object SystemUIChecker {
    /**
     * @return true if the system has the valid smtp account, false if otherwise
     */
    @JvmStatic fun hasValidSmtpAccount(): Boolean {
        if (!SiteConfiguration.isDemandEdition()) {
            val extMailService = AppContextUtil.getSpringBean(ExtMailService::class.java)
            when {
                !extMailService.isMailSetupValid -> {
                    when {
                        UserUIContext.isAdmin() -> ConfirmDialogExt.show(UI.getCurrent(),
                                UserUIContext.getMessage(ShellI18nEnum.WINDOW_STMP_NOT_SETUP),
                                UserUIContext.getMessage(ShellI18nEnum.WINDOW_SMTP_CONFIRM_SETUP_FOR_ADMIN),
                                UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                                UserUIContext.getMessage(GenericI18Enum.ACTION_NO)
                        ) { confirmDialog ->
                            if (confirmDialog.isConfirmed) {
                                Page.getCurrent().javaScript.execute("window.open('https://docs.airback.com/administration/email-configuration/', \"_blank\", \"\");")
                            }
                        }
                        else -> NotificationUtil.showErrorNotification(UserUIContext.getMessage(ShellI18nEnum.WINDOW_SMTP_CONFIRM_SETUP_FOR_USER))
                    }
                    return false
                }
                else -> return true
            }
        }
        return true
    }
}
