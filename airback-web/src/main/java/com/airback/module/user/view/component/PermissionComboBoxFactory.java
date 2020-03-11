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
package com.airback.module.user.view.component;

import com.airback.core.airbackException;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.BooleanPermissionFlag;
import com.airback.security.PermissionFlag;
import com.airback.vaadin.web.ui.KeyCaptionComboBox;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class PermissionComboBoxFactory {
    public static KeyCaptionComboBox createPermissionSelection(Class<? extends PermissionFlag> flag) {
        if (AccessPermissionFlag.class.isAssignableFrom(flag)) {
            return new AccessPermissionComboBox();
        } else if (BooleanPermissionFlag.class.isAssignableFrom(flag)) {
            return new YesNoPermissionComboBox();
        } else {
            throw new airbackException("Do not support permission flag " + flag);
        }
    }
}
