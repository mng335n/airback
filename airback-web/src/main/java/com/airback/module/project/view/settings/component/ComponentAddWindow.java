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
package com.airback.module.project.view.settings.component;

import com.airback.common.i18n.OptionI18nEnum;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.i18n.ComponentI18nEnum;
import com.airback.module.project.view.settings.ComponentDefaultFormLayoutFactory;
import com.airback.module.project.domain.Component;
import com.airback.module.project.service.ComponentService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.IEditFormHandler;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.web.ui.DefaultDynaFormLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import static com.airback.vaadin.web.ui.utils.FormControlsGenerator.generateEditFormControls;

/**
 * @author airback Ltd
 * @since 5.3.0
 */
class ComponentAddWindow extends MWindow implements IEditFormHandler<Component> {
    ComponentAddWindow() {
        super(UserUIContext.getMessage(ComponentI18nEnum.NEW));
        AdvancedEditBeanForm<Component> editForm = new AdvancedEditBeanForm<>();
        editForm.addFormHandler(this);
        editForm.setFormLayoutFactory(new DefaultDynaFormLayout(ProjectTypeConstants.COMPONENT,
                ComponentDefaultFormLayoutFactory.getAddForm(), "id"));
        editForm.setBeanFormFieldFactory(new ComponentEditFormFieldFactory(editForm));
        Component component = new Component();
        component.setProjectid(CurrentProjectVariables.getProjectId());
        component.setSaccountid(AppUI.getAccountId());
        component.setStatus(OptionI18nEnum.StatusI18nEnum.Open.name());
        editForm.setBean(component);
        ComponentContainer buttonControls = generateEditFormControls(editForm, true, false, true);
        this.setContent(new MVerticalLayout(editForm, buttonControls).withAlign(buttonControls, Alignment.TOP_RIGHT));
        this.withWidth("750px").withModal(true).withResizable(false).withCenter();
    }

    @Override
    public void onSave(Component bean) {
        ComponentService componentService = AppContextUtil.getSpringBean(ComponentService.class);
        componentService.saveWithSession(bean, UserUIContext.getUsername());
        close();
    }

    @Override
    public void onCancel() {
        close();
    }
}
