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

import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.ui.components.IBlockContainer;
import com.airback.module.project.ui.components.IGroupComponent;
import com.airback.module.project.ui.components.TicketRowRender;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * @author airback Ltd
 * @since 5.3.5
 */
class DefaultTicketGroupComponent extends MVerticalLayout implements IGroupComponent, IBlockContainer {
    private Label headerLbl;
    private MVerticalLayout wrapBody;

    private String titleValue;

    DefaultTicketGroupComponent(String titleValue) {
        this.titleValue = titleValue;
        this.withMargin(new MarginInfo(true, false, true, false)).withSpacing(false);
        wrapBody = new MVerticalLayout().withSpacing(false).withFullWidth().withStyleName(WebThemes.BORDER_LIST);
        headerLbl = ELabel.h3("").withFullWidth();
        this.with(headerLbl, wrapBody);
        refresh();
    }

    @Override
    public void refresh() {
        if (wrapBody.getComponentCount() > 0) {
            updateTitle();
        } else {
            ComponentContainer parent = (ComponentContainer) getParent();
            if (parent != null) {
                parent.removeComponent(this);
            }
        }
    }

    void insertTicketComp(TicketRowRender ticketRowRender) {
        wrapBody.addComponent(ticketRowRender);
        updateTitle();
    }

    private void updateTitle() {
        headerLbl.setValue(String.format("%s (%d)", titleValue, wrapBody.getComponentCount()));
    }
}
