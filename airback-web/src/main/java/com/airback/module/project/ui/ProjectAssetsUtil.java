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
package com.airback.module.project.ui;

import com.airback.common.domain.Client;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.utils.StringUtils;
import com.airback.module.file.PathUtils;
import com.airback.module.file.StorageUtils;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.i18n.OptionI18nEnum.MilestoneStatus;
import com.airback.module.project.ui.components.ProjectLogoUploadWindow;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * @author airback Ltd.
 * @since 5.0.0
 */
public class ProjectAssetsUtil {

    public static VaadinIcons getPhaseIcon(String status) {
        if (MilestoneStatus.Closed.name().equals(status)) {
            return VaadinIcons.MINUS_CIRCLE;
        } else if (MilestoneStatus.Future.name().equals(status)) {
            return VaadinIcons.CLOCK;
        } else {
            return VaadinIcons.SPINNER;
        }
    }

    public static Component projectLogoComp(String projectShortname, Integer projectId, String projectAvatarId, int size) {
        AbstractComponent wrapper;
        if (!StringUtils.isBlank(projectAvatarId)) {
            wrapper = new Image(null, new ExternalResource(StorageUtils.getResourcePath
                    (String.format("%s/%s_%d.png", PathUtils.getProjectLogoPath(AppUI.getAccountId(), projectId),
                            projectAvatarId, size))));
        } else {
            ELabel projectIcon = new ELabel(projectShortname.substring(0, 1)).withStyleName(WebThemes.TEXT_ELLIPSIS, ValoTheme.LABEL_LARGE, "center");
            projectIcon.setWidth(size, Sizeable.Unit.PIXELS);
            projectIcon.setHeight(size, Sizeable.Unit.PIXELS);
            wrapper = new MVerticalLayout(projectIcon).withAlign(projectIcon, Alignment.MIDDLE_CENTER).withMargin(false);
        }
        wrapper.setWidth(size, Sizeable.Unit.PIXELS);
        wrapper.setHeight(size, Sizeable.Unit.PIXELS);
        wrapper.addStyleName(WebThemes.CIRCLE_BOX);
        wrapper.setDescription(UserUIContext.getMessage(GenericI18Enum.OPT_CHANGE_IMAGE));
        return wrapper;
    }

    public static Component editableProjectLogoComp(String projectShortname, Integer projectId, String projectAvatarId, int size) {
        MCssLayout wrapper = new MCssLayout();

        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.PROJECT)) {
            wrapper.addStyleName(WebThemes.CURSOR_POINTER);
            wrapper.setDescription(UserUIContext.getMessage(GenericI18Enum.OPT_CHANGE_IMAGE));
            wrapper.addLayoutClickListener((LayoutEvents.LayoutClickListener) layoutClickEvent ->
                    UI.getCurrent().addWindow(new ProjectLogoUploadWindow(projectShortname, projectId, projectAvatarId))
            );
        }

        if (!StringUtils.isBlank(projectAvatarId)) {
            Image image = new Image(null, new ExternalResource(StorageUtils.getResourcePath
                    (String.format("%s/%s_%d.png", PathUtils.getProjectLogoPath(AppUI.getAccountId(), projectId),
                            projectAvatarId, size))));
            image.addStyleName(WebThemes.CIRCLE_BOX);
            wrapper.addComponent(image);
        } else {
            ELabel projectIcon = new ELabel(projectShortname.substring(0, 1)).withStyleName(WebThemes.TEXT_ELLIPSIS, ValoTheme.LABEL_LARGE, "center", WebThemes.CIRCLE_BOX).withDescription(projectShortname);
            projectIcon.setWidth(size, Sizeable.Unit.PIXELS);
            projectIcon.setHeight(size, Sizeable.Unit.PIXELS);
            wrapper.addComponent(projectIcon);
        }
        wrapper.setWidth(size, Sizeable.Unit.PIXELS);
        wrapper.setHeight(size, Sizeable.Unit.PIXELS);
        return wrapper;
    }

    public static Component clientLogoComp(Client client, int size) {
        AbstractComponent wrapper;
        if (!StringUtils.isBlank(client.getAvatarid())) {
            wrapper = new Image(null, new ExternalResource(StorageUtils.getEntityLogoPath(AppUI.getAccountId(), client.getAvatarid(), 100)));
        } else {
            String clientName = client.getName();
            clientName = (clientName.length() > 3) ? clientName.substring(0, 3) : clientName;
            ELabel projectIcon = new ELabel(clientName).withStyleName(WebThemes.TEXT_ELLIPSIS, "center");
            wrapper = new VerticalLayout();
            ((VerticalLayout) wrapper).addComponent(projectIcon);
            ((VerticalLayout) wrapper).setComponentAlignment(projectIcon, Alignment.MIDDLE_CENTER);
        }
        wrapper.setWidth(size, Sizeable.Unit.PIXELS);
        wrapper.setHeight(size, Sizeable.Unit.PIXELS);
        wrapper.addStyleName(WebThemes.CIRCLE_BOX);
        wrapper.setDescription(UserUIContext.getMessage(GenericI18Enum.OPT_CHANGE_IMAGE));
        return wrapper;
    }
}
