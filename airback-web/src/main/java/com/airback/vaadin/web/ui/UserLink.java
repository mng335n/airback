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

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Img;
import com.airback.core.utils.StringUtils;
import com.airback.html.DivLessFormatter;
import com.airback.module.file.StorageUtils;
import com.airback.module.user.AccountLinkGenerator;
import com.airback.vaadin.TooltipHelper;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class UserLink extends Label {
    private static final long serialVersionUID = 1L;

    public UserLink(String username, String userAvatarId, String displayName) {
        if (StringUtils.isBlank(username)) {
            return;
        }
        this.setContentMode(ContentMode.HTML);

        DivLessFormatter div = new DivLessFormatter();
        Img userAvatar = new Img("", StorageUtils.getAvatarPath(userAvatarId, 16)).setCSSClass(WebThemes.CIRCLE_BOX);
        A userLink = new A().setId("tag" + TooltipHelper.TOOLTIP_ID).setHref(AccountLinkGenerator.generateUserLink(
                username)).appendText(StringUtils.trim(displayName, 30, true));
        userLink.setAttribute("onmouseover", TooltipHelper.userHoverJsFunction(username));
        userLink.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction());
        div.appendChild(userAvatar, DivLessFormatter.EMPTY_SPACE, userLink);
        this.setValue(div.write());
    }
}
