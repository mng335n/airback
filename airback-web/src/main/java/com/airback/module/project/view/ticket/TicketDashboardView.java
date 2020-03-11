/**
 * Copyright © airback
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.project.view.ticket;

import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.vaadin.mvp.PageView;
import com.airback.vaadin.web.ui.IListView;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public interface TicketDashboardView extends IListView<ProjectTicketSearchCriteria, ProjectTicket>, PageView {

    void displayView(String query);

    void queryTickets(ProjectTicketSearchCriteria searchCriteria);

    ProjectTicketSearchCriteria getCriteria();
}
