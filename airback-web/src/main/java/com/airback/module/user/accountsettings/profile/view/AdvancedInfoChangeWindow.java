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
package com.airback.module.user.accountsettings.profile.view;

import com.google.common.base.MoreObjects;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.form.view.LayoutType;
import com.airback.module.user.accountsettings.localization.UserI18nEnum;
import com.airback.module.user.domain.User;
import com.airback.module.user.service.UserService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.UIUtils;
import com.airback.vaadin.web.ui.CountryComboBox;
import com.airback.vaadin.web.ui.WebThemes;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

/**
 * @author airback Ltd.
 * @since 1.0
 */
class AdvancedInfoChangeWindow extends MWindow {
    private TextField txtWebsite = new TextField();
    private TextField txtCompany = new TextField();
    private CountryComboBox cboCountry = new CountryComboBox();

    private final User user;

    AdvancedInfoChangeWindow(final User user) {
        super(UserUIContext.getMessage(UserI18nEnum.WINDOW_CHANGE_ADVANCED_INFO_TITLE));
        this.user = user;
        this.withWidth("450px").withResizable(false).withModal(true).withCenter();
        this.initUI();
    }

    private void initUI() {
        MVerticalLayout mainLayout = new MVerticalLayout().withMargin(true).withFullWidth();

        GridFormLayoutHelper passInfo = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.ONE_COLUMN);

        passInfo.addComponent(txtWebsite, UserUIContext.getMessage(UserI18nEnum.FORM_WEBSITE), 0, 0);
        passInfo.addComponent(txtCompany, UserUIContext.getMessage(UserI18nEnum.FORM_COMPANY), 0, 1);
        passInfo.addComponent(cboCountry, UserUIContext.getMessage(UserI18nEnum.FORM_COUNTRY), 0, 2);

        txtWebsite.setValue(MoreObjects.firstNonNull(user.getWebsite(), ""));
        txtCompany.setValue(MoreObjects.firstNonNull(user.getCompany(), ""));
        cboCountry.setValue(MoreObjects.firstNonNull(user.getCountry(), ""));

        mainLayout.with(passInfo.getLayout()).withAlign(passInfo.getLayout(), Alignment.TOP_LEFT);

        MButton cancelBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CANCEL), clickEvent -> close())
                .withStyleName(WebThemes.BUTTON_OPTION);

        MButton saveBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_SAVE), clickEvent -> changeInfo())
                .withStyleName(WebThemes.BUTTON_ACTION).withIcon(VaadinIcons.CLIPBOARD).withClickShortcut(KeyCode.ENTER);

        MHorizontalLayout buttonControls = new MHorizontalLayout(cancelBtn, saveBtn);
        mainLayout.with(buttonControls).withAlign(buttonControls, Alignment.MIDDLE_RIGHT);
        this.setModal(true);
        this.setContent(mainLayout);
    }

    private void changeInfo() {
        user.setWebsite(txtWebsite.getValue());
        user.setCompany(txtCompany.getValue());
        user.setCountry((String) cboCountry.getValue());

        UserService userService = AppContextUtil.getSpringBean(UserService.class);
        userService.updateWithSession(user, UserUIContext.getUsername());
        close();
        UIUtils.reloadPage();
    }
}
