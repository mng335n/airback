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
package com.airback.vaadin.web.ui;

import com.airback.common.i18n.ShellI18nEnum;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.CacheableComponent;
import org.vaadin.viritin.layouts.MWindow;

/**
 * @author airback Ltd
 * @since 5.2.6
 */
public abstract class AbstractAboutWindow extends MWindow implements CacheableComponent {
    public AbstractAboutWindow() {
        super(UserUIContext.getMessage(ShellI18nEnum.OPT_ABOUT_airback));
        this.withModal(true).withResizable(false).withWidth("600px").withCenter();
    }
}
