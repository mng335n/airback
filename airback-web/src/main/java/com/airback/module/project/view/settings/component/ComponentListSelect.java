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
package com.airback.module.project.view.settings.component;

import com.airback.common.i18n.OptionI18nEnum;
import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.Component;
import com.airback.module.project.domain.criteria.ComponentSearchCriteria;
import com.airback.module.project.service.ComponentService;
import com.airback.spring.AppContextUtil;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.ListSelect;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 4.5.0
 */
public class ComponentListSelect extends ListSelect<Component> {
    private static final long serialVersionUID = 1L;

    public ComponentListSelect() {
        this.setRows(4);
        ComponentSearchCriteria searchCriteria = new ComponentSearchCriteria();
        searchCriteria.setStatus(StringSearchField.and(OptionI18nEnum.StatusI18nEnum.Open.name()));
        searchCriteria.setProjectId(NumberSearchField.equal(CurrentProjectVariables.getProjectId()));

        ComponentService componentService = AppContextUtil.getSpringBean(ComponentService.class);
        List<Component> components = (List<Component>) componentService.findPageableListByCriteria(new BasicSearchRequest<>(searchCriteria));
        this.setItems(components);
        this.setItemCaptionGenerator((ItemCaptionGenerator<Component>) Component::getName);
    }
}
