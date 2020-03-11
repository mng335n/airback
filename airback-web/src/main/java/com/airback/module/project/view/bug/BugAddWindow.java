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
package com.airback.module.project.view.bug;

import com.airback.module.project.i18n.BugI18nEnum;
import com.airback.module.project.domain.SimpleBug;
import com.airback.vaadin.UserUIContext;
import org.vaadin.viritin.layouts.MWindow;

/**
 * @author airback Ltd
 * @since 5.2.0
 */
public class BugAddWindow extends MWindow {
    public BugAddWindow(SimpleBug bug) {
        if (bug.getId() == null) {
            setCaption(UserUIContext.getMessage(BugI18nEnum.NEW));
        } else {
            setCaption(UserUIContext.getMessage(BugI18nEnum.SINGLE) + ": " + bug.getName());
        }

        BugEditForm editForm = new BugEditForm() {
            @Override
            protected void postExecution() {
                close();
            }
        };
        editForm.setBean(bug);

        withWidth("1200px").withModal(true).withResizable(false).withContent(editForm).withCenter();
    }
}
