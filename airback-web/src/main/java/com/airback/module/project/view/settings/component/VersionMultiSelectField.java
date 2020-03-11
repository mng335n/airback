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

import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.Version;
import com.airback.module.project.domain.criteria.VersionSearchCriteria;
import com.airback.module.project.service.VersionService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.web.ui.MultiSelectComp;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.ui.UI;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class VersionMultiSelectField extends MultiSelectComp<Version> {
    private static final long serialVersionUID = 1L;

    public VersionMultiSelectField() {
        super("name", true);
        this.setWidth(WebThemes.FORM_CONTROL_WIDTH);
    }

    @Override
    protected List<Version> createData() {
        VersionSearchCriteria searchCriteria = new VersionSearchCriteria();
        searchCriteria.setStatus(StringSearchField.and(StatusI18nEnum.Open.name()));
        searchCriteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
        VersionService versionService = AppContextUtil.getSpringBean(VersionService.class);
        return (List<Version>) versionService.findPageableListByCriteria(new BasicSearchRequest<>(searchCriteria));
    }

    @Override
    protected void requestAddNewComp() {
        UI.getCurrent().addWindow(new VersionAddWindow());
    }

    @Override
    protected void doSetValue(List<Version> value) {
        setSelectedItems(value);
    }

    @Override
    public List<Version> getValue() {
        return selectedItems;
    }
}
