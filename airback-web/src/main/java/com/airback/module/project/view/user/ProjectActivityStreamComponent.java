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
package com.airback.module.project.view.user;

import com.airback.common.ModuleNameConstants;
import com.airback.common.domain.criteria.ActivityStreamSearchCriteria;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.vaadin.AppUI;
import org.vaadin.viritin.layouts.MCssLayout;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectActivityStreamComponent extends MCssLayout {
    private static final long serialVersionUID = 1L;

    public void showProjectFeeds() {
        this.removeAllComponents();
        ProjectActivityStreamPagedList activityStreamList = new ProjectActivityStreamPagedList();
        this.addComponent(activityStreamList);
        ActivityStreamSearchCriteria searchCriteria = new ActivityStreamSearchCriteria();
        searchCriteria.setModuleSet(new SetSearchField<>(ModuleNameConstants.PRJ));
        searchCriteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
        searchCriteria.setExtraTypeIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
        activityStreamList.setSearchCriteria(searchCriteria);
    }
}
