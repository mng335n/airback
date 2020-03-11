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
package com.airback.module.project.view.ticket;

import com.airback.configuration.SiteConfiguration;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.i18n.BugI18nEnum;
import com.airback.module.project.i18n.RiskI18nEnum;
import com.airback.module.project.i18n.TaskI18nEnum;
import com.airback.vaadin.UserUIContext;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.ListSelect;

/**
 * @author airback Ltd
 * @since 5.4.3
 */
public class TicketTypeListSelect extends ListSelect<String> {
    public TicketTypeListSelect() {
        this.setRows(3);

        if (!SiteConfiguration.isCommunityEdition()) {
            this.setItems(ProjectTypeConstants.TASK, ProjectTypeConstants.BUG, ProjectTypeConstants.RISK);
        } else {
            this.setItems(ProjectTypeConstants.TASK, ProjectTypeConstants.BUG);
        }

        this.setItemCaptionGenerator((ItemCaptionGenerator<String>) item -> {
            if (ProjectTypeConstants.TASK.equals(item)) {
                return UserUIContext.getMessage(TaskI18nEnum.SINGLE);
            } else if (ProjectTypeConstants.BUG.equals(item)) {
                return UserUIContext.getMessage(BugI18nEnum.SINGLE);
            } else if (ProjectTypeConstants.RISK.equals(item)) {
                return UserUIContext.getMessage(RiskI18nEnum.SINGLE);
            } else {
                return "";
            }
        });
    }
}
