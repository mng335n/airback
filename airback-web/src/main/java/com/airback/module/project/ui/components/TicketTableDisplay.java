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
package com.airback.module.project.ui.components;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Text;
import com.airback.common.TableViewField;
import com.airback.html.DivLessFormatter;
import com.airback.module.project.ProjectLinkGenerator;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.service.ProjectTicketService;
import com.airback.module.project.ui.ProjectAssetsManager;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.TooltipHelper;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.WebThemes;
import com.airback.vaadin.web.ui.table.DefaultPagedBeanTable;
import org.vaadin.viritin.button.MButton;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class TicketTableDisplay extends DefaultPagedBeanTable<ProjectTicketService, ProjectTicketSearchCriteria, ProjectTicket> {
    private static final long serialVersionUID = 1L;

    public TicketTableDisplay(List<TableViewField> displayColumns) {
        super(AppContextUtil.getSpringBean(ProjectTicketService.class), ProjectTicket.class, displayColumns);

        addGeneratedColumn("name", (source, itemId, columnId) -> {
            ProjectTicket ticket = getBeanByIndex(itemId);

            Div div = new DivLessFormatter();
            Text image = new Text(ProjectAssetsManager.getAsset(ticket.getType()).getHtml());
            A itemLink = new A().setId("tag" + TooltipHelper.TOOLTIP_ID);
            if (ProjectTypeConstants.TASK.equals(ticket.getType()) || ProjectTypeConstants.BUG.equals(ticket.getType()) || ProjectTypeConstants.RISK.equals(ticket.getType())) {
                itemLink.setHref(ProjectLinkGenerator.generateProjectItemLink(ticket.getProjectShortName(),
                        ticket.getProjectId(), ticket.getType(), ticket.getExtraTypeId() + ""));
            } else {
                itemLink.setHref(ProjectLinkGenerator.generateProjectItemLink(ticket.getProjectShortName(),
                        ticket.getProjectId(), ticket.getType(), ticket.getTypeId() + ""));
            }

            itemLink.setAttribute("onmouseover", TooltipHelper.projectHoverJsFunction(ticket.getType(), ticket.getTypeId() + ""));
            itemLink.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction());
            itemLink.appendText(ProjectAssetsManager.getAsset(ticket.getType()).getHtml() + " " + ticket.getName());

            div.appendChild(image, DivLessFormatter.EMPTY_SPACE, itemLink);

            MButton assignmentLink = new MButton(div.write(),
                    clickEvent -> fireTableEvent(new TableClickEvent(TicketTableDisplay.this, ticket, "name")))
                    .withStyleName(WebThemes.BUTTON_LINK);
            assignmentLink.setCaptionAsHtml(true);
            return assignmentLink;
        });

        addGeneratedColumn("assignUser", (source, itemId, columnId) -> {
            ProjectTicket task = getBeanByIndex(itemId);
            return new ProjectMemberLink(task.getAssignUser(), task.getAssignUserAvatarId(), task.getAssignUserFullName());
        });

        addGeneratedColumn("dueDate", (source, itemId, columnId) -> {
            ProjectTicket task = getBeanByIndex(itemId);
            return new ELabel().prettyDate(task.getDueDate());
        });
    }
}
