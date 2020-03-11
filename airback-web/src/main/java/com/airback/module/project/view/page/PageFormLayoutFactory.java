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
package com.airback.module.project.view.page;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.form.view.LayoutType;
import com.airback.module.project.i18n.PageI18nEnum;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.AbstractFormLayoutFactory;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * @author airback Ltd.
 * @since 4.4.0
 */
public class PageFormLayoutFactory extends AbstractFormLayoutFactory {

    private GridFormLayoutHelper informationLayout;

    @Override
    public AbstractComponent getLayout() {
        informationLayout = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.ONE_COLUMN);
        return informationLayout.getLayout();
    }

    @Override
    protected HasValue<?> onAttachField(Object propertyId, HasValue<?> field) {
        if (propertyId.equals("subject")) {
            return informationLayout.addComponent(field, UserUIContext.getMessage(PageI18nEnum.FORM_SUBJECT), 0, 0);
        } else if (propertyId.equals("content")) {
            return informationLayout.addComponent(field, UserUIContext.getMessage(GenericI18Enum.FORM_DESCRIPTION), 0, 1);
        } else if (propertyId.equals("status")) {
            return informationLayout.addComponent(field, UserUIContext.getMessage(PageI18nEnum.FORM_VISIBILITY), 0, 2);
        }
        return null;
    }
}
