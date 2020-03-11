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
import com.airback.module.project.ui.components.TicketRowRender;
import com.airback.vaadin.web.ui.WebThemes;

import java.util.List;

/**
 * @author airback Ltd
 * @since 5.1.1
 */
public class SimpleListOrderComponent extends TicketGroupOrderComponent {
    public SimpleListOrderComponent() {
        super();
        this.addStyleName(WebThemes.BORDER_LIST);
    }

    public SimpleListOrderComponent(Class<? extends TicketRowRender> ticketRowRenderCls) {
        super(ticketRowRenderCls);
        this.addStyleName(WebThemes.BORDER_LIST);
    }

    @Override
    public void insertTickets(List<ProjectTicket> tickets) {
        tickets.stream().map(EditableTicketRowRenderer::new).forEach(this::addComponent);
    }
}