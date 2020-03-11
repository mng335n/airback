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
package com.airback.module.project.view.settings;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria;
import com.airback.module.project.event.ProjectRoleEvent;
import com.airback.module.project.i18n.ProjectRoleI18nEnum;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.HeaderWithIcon;
import com.airback.vaadin.web.ui.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectRoleSearchPanel extends DefaultGenericSearchPanel<ProjectRoleSearchCriteria> {
    private static final long serialVersionUID = 1L;

    @Override
    protected SearchLayout<ProjectRoleSearchCriteria> createBasicSearchLayout() {
        return new ProjectRoleBasicSearchLayout();
    }

    @Override
    protected SearchLayout<ProjectRoleSearchCriteria> createAdvancedSearchLayout() {
        return null;
    }

    @Override
    protected HeaderWithIcon buildSearchTitle() {
        return HeaderWithIcon.h2(VaadinIcons.CLIPBOARD_USER, UserUIContext.getMessage(ProjectRoleI18nEnum.LIST));
    }

    @Override
    protected Component buildExtraControls() {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.ROLES)) {
            return new MButton(UserUIContext.getMessage(ProjectRoleI18nEnum.NEW),
                    clickEvent -> EventBusFactory.getInstance().post(new ProjectRoleEvent.GotoAdd(this, null)))
                    .withIcon(VaadinIcons.PLUS).withStyleName(WebThemes.BUTTON_ACTION);
        } else return null;
    }

    private class ProjectRoleBasicSearchLayout extends BasicSearchLayout<ProjectRoleSearchCriteria> {
        private static final long serialVersionUID = 1L;
        private TextField nameField;

        private ProjectRoleBasicSearchLayout() {
            super(ProjectRoleSearchPanel.this);
        }

        @Override
        public ComponentContainer constructBody() {
            MHorizontalLayout basicSearchBody = new MHorizontalLayout().withMargin(true);
            basicSearchBody.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            basicSearchBody.addComponent(new Label(UserUIContext.getMessage(GenericI18Enum.FORM_NAME) + ":"));
            nameField = new MTextField().withPlaceholder(UserUIContext.getMessage(GenericI18Enum.ACTION_QUERY_BY_TEXT))
                    .withWidth(WebUIConstants.DEFAULT_CONTROL_WIDTH);
            basicSearchBody.addComponent(nameField);

            MButton searchBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_SEARCH), clickEvent -> callSearchAction())
                    .withIcon(VaadinIcons.SEARCH).withStyleName(WebThemes.BUTTON_ACTION)
                    .withClickShortcut(ShortcutAction.KeyCode.ENTER);
            basicSearchBody.addComponent(searchBtn);

            MButton clearBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CLEAR), clickEvent -> nameField.setValue(""))
                    .withStyleName(WebThemes.BUTTON_OPTION);
            basicSearchBody.addComponent(clearBtn);
            return basicSearchBody;
        }

        @Override
        protected ProjectRoleSearchCriteria fillUpSearchCriteria() {
            ProjectRoleSearchCriteria searchCriteria = new ProjectRoleSearchCriteria();
            searchCriteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));
            searchCriteria.setRoleName(StringSearchField.and(nameField.getValue()));
            return searchCriteria;
        }
    }
}