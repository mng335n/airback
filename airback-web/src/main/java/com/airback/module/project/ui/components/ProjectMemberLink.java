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
package com.airback.module.project.ui.components;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Img;
import com.airback.core.utils.StringUtils;
import com.airback.html.DivLessFormatter;
import com.airback.module.file.StorageUtils;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectLinkGenerator;
import com.airback.vaadin.TooltipHelper;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

/**
 * @author airback Ltd.
 * @version 5.0.4
 */
public class ProjectMemberLink extends Label {
    public ProjectMemberLink(String username, String userAvatarId, String displayName) {
        this(CurrentProjectVariables.getProjectId(), username, userAvatarId, displayName);
    }

    public ProjectMemberLink(Integer projectId, String username, String userAvatarId, String displayName) {
        if (StringUtils.isBlank(username)) {
            return;
        }
        this.setContentMode(ContentMode.HTML);

        DivLessFormatter div = new DivLessFormatter();
        Img userAvatar = new Img("", StorageUtils.getAvatarPath(userAvatarId, 16)).setCSSClass(WebThemes.CIRCLE_BOX);
        A userLink = new A().setId("tag" + TooltipHelper.TOOLTIP_ID).
                setHref(ProjectLinkGenerator.generateProjectMemberLink(projectId, username))
                .appendText(StringUtils.trim(displayName, 30, true));
        userLink.setAttribute("onmouseover", TooltipHelper.userHoverJsFunction(username));
        userLink.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction());
        div.appendChild(userAvatar, DivLessFormatter.EMPTY_SPACE, userLink);
        this.setValue(div.write());
    }
}
