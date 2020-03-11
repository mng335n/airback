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
package com.airback.module.project.view;

import com.airback.common.i18n.ErrorI18nEnum;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.utils.StringUtils;
import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SearchField;
import com.airback.form.view.LayoutType;
import com.airback.module.project.domain.SimpleProject;
import com.airback.module.project.domain.criteria.ProjectSearchCriteria;
import com.airback.module.project.event.ProjectEvent;
import com.airback.module.project.i18n.ProjectI18nEnum;
import com.airback.module.project.service.ProjectService;
import com.airback.module.project.service.ProjectTemplateService;
import com.airback.module.project.view.parameters.ProjectScreenData;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.PageActionChain;
import com.airback.vaadin.ui.NotificationUtil;
import com.airback.vaadin.web.ui.WebThemes;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.TextField;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.util.List;

import static com.airback.common.i18n.GenericI18Enum.FORM_NAME;
import static com.airback.module.project.i18n.ProjectI18nEnum.*;

/**
 * @author airback Ltd
 * @since 5.2.8
 */
public class ProjectAddBaseTemplateWindow extends MWindow {
    public ProjectAddBaseTemplateWindow() {
        super(UserUIContext.getMessage(OPT_CREATE_PROJECT_FROM_TEMPLATE));
        this.withModal(true).withClosable(true).withResizable(false).withWidth("550px");
        MVerticalLayout content = new MVerticalLayout();
        GridFormLayoutHelper gridFormLayoutHelper = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.ONE_COLUMN);
        final TemplateProjectComboBox templateProjectComboBox = new TemplateProjectComboBox();
        gridFormLayoutHelper.addComponent(templateProjectComboBox, UserUIContext.getMessage(FORM_TEMPLATE),
                UserUIContext.getMessage(OPT_MARK_TEMPLATE_HELP), 0, 0);
        final TextField prjNameField = new TextField();
        gridFormLayoutHelper.addComponent(prjNameField, UserUIContext.getMessage(FORM_NAME), 0, 1);
        final TextField prjKeyField = new TextField();
        gridFormLayoutHelper.addComponent(prjKeyField, UserUIContext.getMessage(FORM_SHORT_NAME), 0, 2);

        MButton okBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_OK), clickEvent -> {
            SimpleProject templatePrj = (SimpleProject) templateProjectComboBox.getValue();
            if (templatePrj == null) {
                NotificationUtil.showErrorNotification(UserUIContext.getMessage(ERROR_MUST_CHOOSE_TEMPLATE_PROJECT));
                return;
            }
            String newPrjName = prjNameField.getValue();
            if (newPrjName.length() == 0) {
                NotificationUtil.showErrorNotification(UserUIContext.getMessage(ErrorI18nEnum.FIELD_MUST_NOT_NULL,
                        UserUIContext.getMessage(GenericI18Enum.FORM_NAME)));
                return;
            }
            String newPrjKey = prjKeyField.getValue();
            if (newPrjKey.length() > 3 || newPrjKey.length() == 0) {
                NotificationUtil.showErrorNotification(UserUIContext.getMessage(ProjectI18nEnum.ERROR_PROJECT_KEY_INVALID));
                return;
            }
            ProjectTemplateService projectTemplateService = AppContextUtil.getSpringBean
                    (ProjectTemplateService.class);
            if (projectTemplateService != null) {
                Integer newProjectId = projectTemplateService.cloneProject(templatePrj.getId(), newPrjName, newPrjKey,
                        AppUI.getAccountId(), UserUIContext.getUsername());
                EventBusFactory.getInstance().post(new ProjectEvent.GotoMyProject(this,
                        new PageActionChain(new ProjectScreenData.Goto(newProjectId))));
                close();
            }
        }).withIcon(VaadinIcons.CLIPBOARD).withStyleName(WebThemes.BUTTON_ACTION);
        MButton cancelBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CANCEL), clickEvent -> close())
                .withStyleName(WebThemes.BUTTON_OPTION);
        MHorizontalLayout buttonControls = new MHorizontalLayout(cancelBtn, okBtn);
        content.with(gridFormLayoutHelper.getLayout(), buttonControls).withAlign(buttonControls, Alignment.MIDDLE_RIGHT);
        this.setContent(content);
    }

    private static class TemplateProjectComboBox extends ComboBox<SimpleProject> {
        TemplateProjectComboBox() {
            ProjectService projectService = AppContextUtil.getSpringBean(ProjectService.class);
            ProjectSearchCriteria searchCriteria = new ProjectSearchCriteria();
            searchCriteria.addExtraField(ProjectSearchCriteria.p_template.buildParamIsEqual(SearchField.AND, 1));
            searchCriteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
            List<SimpleProject> projectTemplates = (List<SimpleProject>) projectService.findPageableListByCriteria(new BasicSearchRequest<>(searchCriteria));
            this.setItems(projectTemplates);
            this.setItemCaptionGenerator((ItemCaptionGenerator<SimpleProject>) item -> StringUtils.trim(String.format("[%s] %s", item.getShortname(),
                    item.getName()), 50, true));
        }
    }
}
