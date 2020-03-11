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
package com.airback.module.project.view.milestone;

import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.Milestone;
import com.airback.module.project.domain.SimpleMilestone;
import com.airback.module.project.domain.criteria.MilestoneSearchCriteria;
import com.airback.module.project.service.MilestoneService;
import com.airback.spring.AppContextUtil;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.ListSelect;

import java.util.List;

/**
 * @author airback Ltd
 * @since 5.1.1
 */
public class MilestoneListSelect extends ListSelect<SimpleMilestone> {

    public MilestoneListSelect() {
        this.setRows(4);

        MilestoneService milestoneService = AppContextUtil.getSpringBean(MilestoneService.class);
        MilestoneSearchCriteria criteria = new MilestoneSearchCriteria();
        criteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
        List<SimpleMilestone> milestones = (List<SimpleMilestone>) milestoneService.findPageableListByCriteria(new BasicSearchRequest<>(criteria));
        this.setItems(milestones);
        this.setItemCaptionGenerator((ItemCaptionGenerator<SimpleMilestone>) Milestone::getName);
    }
}
