/**
 * Copyright Â© airback
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

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.utils.DateTimeUtils;
import com.airback.core.utils.SortedArrayMap;
import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.ui.components.TicketRowRender;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.vaadin.icons.VaadinIcons;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author airback Ltd
 * @since 5.1.1
 */
public class DueDateOrderComponent extends TicketGroupOrderComponent {
    private SortedArrayMap<Long, DefaultTicketGroupComponent> dueDateAvailables = new SortedArrayMap<>();
    private DefaultTicketGroupComponent unspecifiedTasks;

    public DueDateOrderComponent() {
        super();
    }

    public DueDateOrderComponent(Class<? extends TicketRowRender> ticketRowRenderCls) {
        super(ticketRowRenderCls);
    }

    @Override
    public void insertTickets(List<ProjectTicket> tickets) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppUI.getLongDateFormat()).withLocale(UserUIContext.getUserLocale());
        for (ProjectTicket ticket : tickets) {
            if (ticket.getDueDate() != null) {
                LocalDate dueDate = ticket.getDueDate();
                LocalDate firstDayInWeek = DateTimeUtils.getFirstDayOfWeek(dueDate);
                String monDayStr = formatter.format(firstDayInWeek);
                Long time = firstDayInWeek.toEpochDay();
                if (dueDateAvailables.containsKey(time)) {
                    DefaultTicketGroupComponent groupComponent = dueDateAvailables.get(time);
                    groupComponent.insertTicketComp(buildRenderer(ticket));
                } else {
                    LocalDate maxValue = DateTimeUtils.getLastDayOfWeek(dueDate);
                    String sundayStr = formatter.format(maxValue);
                    String titleValue = VaadinIcons.CALENDAR.getHtml() + " " + String.format("%s - %s", monDayStr, sundayStr);

                    DefaultTicketGroupComponent groupComponent = new DefaultTicketGroupComponent(titleValue);
                    dueDateAvailables.put(time, groupComponent);
                    addComponent(groupComponent);
                    groupComponent.insertTicketComp(buildRenderer(ticket));
                }
            } else {
                if (unspecifiedTasks == null) {
                    unspecifiedTasks = new DefaultTicketGroupComponent(VaadinIcons.CALENDAR.getHtml() + " " + UserUIContext.getMessage(GenericI18Enum.OPT_UNDEFINED));
                    addComponent(unspecifiedTasks);
                }
                unspecifiedTasks.insertTicketComp(buildRenderer(ticket));
            }
        }
    }
}