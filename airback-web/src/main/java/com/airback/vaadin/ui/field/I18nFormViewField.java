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
package com.airback.vaadin.ui.field;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.utils.StringUtils;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

/**
 * @author airback Ltd.
 * @since 4.5.3
 */
public class I18nFormViewField extends CustomField<String> {
    private static final long serialVersionUID = 1L;

    private Class<? extends Enum> enumCls;
    private Enum defaultValue;
    private ELabel label;

    public I18nFormViewField(Class<? extends Enum> enumCls) {
        this(enumCls, null);
    }

    public I18nFormViewField(Class<? extends Enum> enumCls, Enum defaultValue) {
        this.enumCls = enumCls;
        this.defaultValue = defaultValue;
        label = new ELabel(UserUIContext.getMessage(defaultValue), ContentMode.TEXT).withUndefinedWidth().withStyleName(WebThemes.LABEL_WORD_WRAP);
    }

    public I18nFormViewField withStyleName(String styleName) {
        label.addStyleName(styleName);
        return this;
    }

    @Override
    protected Component initContent() {
        return label;
    }

    @Override
    protected void doSetValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            label.setValue(UserUIContext.getMessage(enumCls, value));
        } else if (defaultValue != null) {
            label.setValue(UserUIContext.getMessage(defaultValue));
        }
    }

    @Override
    public String getValue() {
        return null;
    }
}
