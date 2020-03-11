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
package com.airback.vaadin.web.ui.utils;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComponentContainer;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * @author airback Ltd
 * @since 5.3.2
 */
public class FormControlsGenerator {
    public static <T> ComponentContainer generateEditFormControls(final AdvancedEditBeanForm<T> editForm) {
        return generateEditFormControls(editForm, true, true, true);
    }

    public static <T> ComponentContainer generateEditFormControls(final AdvancedEditBeanForm<T> editForm, boolean
            isSaveBtnVisible, boolean isSaveAndNewBtnVisible, boolean isCancelBtnVisible) {
        MHorizontalLayout layout = new MHorizontalLayout();

        if (isCancelBtnVisible) {
            MButton cancelBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CANCEL), clickEvent -> editForm.fireCancelForm())
                    .withStyleName(WebThemes.BUTTON_OPTION);
            layout.addComponent(cancelBtn);
        }

        if (isSaveAndNewBtnVisible) {
            MButton saveAndNewBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_SAVE_NEW), clickEvent -> {
                if (editForm.validateForm()) {
                    editForm.fireSaveAndNewForm();
                }
            }).withIcon(VaadinIcons.CLIPBOARD_CROSS).withStyleName(WebThemes.BUTTON_ACTION);
            if (!isSaveBtnVisible) {
                saveAndNewBtn.withClickShortcut(KeyCode.ENTER);
            }
            layout.addComponent(saveAndNewBtn);
        }

        if (isSaveBtnVisible) {
            MButton saveBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_SAVE), clickEvent -> {
                if (editForm.validateForm()) {
                    editForm.fireSaveForm();
                }
            }).withIcon(VaadinIcons.CLIPBOARD).withStyleName(WebThemes.BUTTON_ACTION)
                    .withClickShortcut(KeyCode.ENTER);
            layout.addComponent(saveBtn);
        }

        return layout;
    }
}
