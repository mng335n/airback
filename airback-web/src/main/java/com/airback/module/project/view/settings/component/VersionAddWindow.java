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

import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.i18n.VersionI18nEnum;
import com.airback.module.project.view.settings.VersionDefaultFormLayoutFactory;
import com.airback.module.project.domain.Version;
import com.airback.module.project.service.VersionService;
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
class VersionAddWindow extends MWindow implements IEditFormHandler<Version> {
    VersionAddWindow() {
        super(UserUIContext.getMessage(VersionI18nEnum.NEW));
        AdvancedEditBeanForm<Version> editForm = new AdvancedEditBeanForm<>();
        editForm.addFormHandler(this);
        editForm.setFormLayoutFactory(new DefaultDynaFormLayout(ProjectTypeConstants.VERSION,
                VersionDefaultFormLayoutFactory.getAddForm(), "id"));
        editForm.setBeanFormFieldFactory(new VersionEditFormFieldFactory(editForm));
        Version version = new Version();
        version.setProjectid(CurrentProjectVariables.getProjectId());
        version.setSaccountid(AppUI.getAccountId());
        version.setStatus(StatusI18nEnum.Open.name());
        editForm.setBean(version);
        ComponentContainer buttonControls = generateEditFormControls(editForm,
                CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.VERSIONS),
                false, true);
        withWidth("750px").withModal(true).withResizable(false).withContent(new MVerticalLayout(editForm,
                buttonControls).withAlign(buttonControls, Alignment.TOP_RIGHT)).withCenter();
    }

    @Override
    public void onSave(Version bean) {
        VersionService versionService = AppContextUtil.getSpringBean(VersionService.class);
        versionService.saveWithSession(bean, UserUIContext.getUsername());
        close();
    }

    @Override
    public void onSaveAndNew(Version bean) {

    }

    @Override
    public void onCancel() {
        close();
    }
}
