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
package com.airback.module.project.view.task;

import com.airback.common.domain.OptionVal;
import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.common.service.OptionValService;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.ui.AbstractOptionValComboBox;
import com.airback.vaadin.web.ui.WebThemes;

import java.util.List;

/**
 * @author airback Ltd
 * @since 5.1.1
 */
public class TaskStatusComboBox extends AbstractOptionValComboBox<StatusI18nEnum> {

    public TaskStatusComboBox() {
        super(StatusI18nEnum.class);
        setWidth(WebThemes.FORM_CONTROL_WIDTH);
    }

    @Override
    protected List<OptionVal> loadOptions() {
        OptionValService optionValService = AppContextUtil.getSpringBean(OptionValService.class);
        return optionValService.findOptionVals(ProjectTypeConstants.TASK,
                CurrentProjectVariables.getProjectId(), AppUI.getAccountId());
    }
}
