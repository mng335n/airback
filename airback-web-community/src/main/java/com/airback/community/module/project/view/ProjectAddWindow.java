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
package com.airback.community.module.project.view;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.module.project.domain.Project;
import com.airback.module.project.event.ProjectEvent;
import com.airback.module.project.service.ProjectService;
import com.airback.module.project.view.AbstractProjectAddWindow;
import com.airback.module.project.view.ProjectGeneralInfoStep;
import com.airback.module.project.view.parameters.ProjectScreenData;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.PageActionChain;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * @author airback Ltd
 * @since 5.3.5
 */
@ViewComponent
public class ProjectAddWindow extends AbstractProjectAddWindow {

    private ProjectGeneralInfoStep projectInfo;

    public ProjectAddWindow() {
        super(new Project().withSaccountid(AppUI.getAccountId()));

        MVerticalLayout contentLayout = new MVerticalLayout().withSpacing(false).withMargin(new MarginInfo(false, false, true, false));
        setContent(contentLayout);
        projectInfo = new ProjectGeneralInfoStep(project);
        contentLayout.addComponent(projectInfo.getContent());

        MButton saveBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_SAVE), clickEvent -> {
            boolean isValid = projectInfo.commit();
            if (isValid) {
                ProjectService projectService = AppContextUtil.getSpringBean(ProjectService.class);
                project.setSaccountid(AppUI.getAccountId());
                projectService.saveWithSession(project, UserUIContext.getUsername());

                EventBusFactory.getInstance().post(new ProjectEvent.GotoMyProject(this,
                        new PageActionChain(new ProjectScreenData.Goto(project.getId()))));
                close();
            }
        }).withIcon(VaadinIcons.CLIPBOARD).withStyleName(WebThemes.BUTTON_ACTION).withClickShortcut(KeyCode.ENTER);

        MButton cancelBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CANCEL), clickEvent -> close())
                .withStyleName(WebThemes.BUTTON_OPTION);
        MHorizontalLayout buttonControls = new MHorizontalLayout(cancelBtn, saveBtn).withMargin(new MarginInfo(true,
                false, false, false));
        contentLayout.with(buttonControls).withAlign(buttonControls, Alignment.MIDDLE_RIGHT);
    }
}
