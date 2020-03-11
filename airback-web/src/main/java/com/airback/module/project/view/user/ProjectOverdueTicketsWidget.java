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
package com.airback.module.project.view.user;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.utils.DateTimeUtils;
import com.airback.db.arguments.DateSearchField;
import com.airback.db.arguments.SearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.i18n.ProjectI18nEnum;
import com.airback.module.project.i18n.TicketI18nEnum;
import com.airback.module.project.service.ProjectTicketService;
import com.airback.module.project.view.ticket.TicketRowDisplayHandler;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.web.ui.DefaultBeanPagedList;
import com.airback.vaadin.web.ui.Depot;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;

import java.text.MessageFormat;

/**
 * @author airback Ltd.
 * @since 4.1
 */
public class ProjectOverdueTicketsWidget extends Depot {
    private static final long serialVersionUID = 1L;

    private ProjectTicketSearchCriteria searchCriteria;

    private DefaultBeanPagedList<ProjectTicketService, ProjectTicketSearchCriteria, ProjectTicket> ticketList;

    public ProjectOverdueTicketsWidget() {
        super(String.format("%s (0)", UserUIContext.getMessage(TicketI18nEnum.VAL_OVERDUE_TICKETS)), new CssLayout());
        this.setWidth("100%");

        CheckBox myItemsSelection = new CheckBox(UserUIContext.getMessage(GenericI18Enum.OPT_MY_ITEMS));
        myItemsSelection.addValueChangeListener(valueChangeEvent -> {
            boolean isMyItemsOption = myItemsSelection.getValue();
            if (isMyItemsOption) {
                searchCriteria.setAssignUser(StringSearchField.and(UserUIContext.getUsername()));
            } else {
                searchCriteria.setAssignUser(null);
            }
            updateSearchResult();
        });

        ticketList = new DefaultBeanPagedList(AppContextUtil.getSpringBean(ProjectTicketService.class),
                new TicketRowDisplayHandler(false), 10) {
            @Override
            protected String stringWhenEmptyList() {
                return UserUIContext.getMessage(ProjectI18nEnum.OPT_NO_OVERDUE_TICKET);
            }
        };
        this.addHeaderElement(myItemsSelection);
        bodyContent.addComponent(ticketList);
    }

    public void showOpenTickets() {
        searchCriteria = new ProjectTicketSearchCriteria();
        searchCriteria.setOpen(new SearchField());
        searchCriteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
        searchCriteria.setDueDate(new DateSearchField(DateTimeUtils.getCurrentDateWithoutMS().toLocalDate(),
                DateSearchField.LESS_THAN));
        updateSearchResult();
    }

    private void updateSearchResult() {
        ticketList.setSearchCriteria(searchCriteria);
        this.setTitle(MessageFormat.format("{0} ({1})", UserUIContext.getMessage(TicketI18nEnum.VAL_OVERDUE_TICKETS),
                ticketList.getTotalCount()));
    }
}