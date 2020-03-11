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
package com.airback.module.project.view.bug;

import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.i18n.BugI18nEnum;
import com.airback.module.project.ui.ProjectAssetsManager;
import com.airback.module.project.ui.components.AbstractEditItemComp;
import com.airback.module.project.domain.Component;
import com.airback.module.project.domain.SimpleBug;
import com.airback.module.project.domain.Version;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasEditFormHandlers;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.ui.IFormLayoutFactory;
import com.airback.vaadin.web.ui.DefaultDynaFormLayout;
import com.airback.vaadin.web.ui.field.AttachmentUploadField;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComponentContainer;

import java.util.List;

import static com.airback.vaadin.web.ui.utils.FormControlsGenerator.generateEditFormControls;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class BugAddViewImpl extends AbstractEditItemComp<SimpleBug> implements BugAddView {
    private static final long serialVersionUID = 1L;

    private BugEditFormFieldFactory editFormFieldFactory;

    @Override
    public AttachmentUploadField getAttachUploadField() {
        return editFormFieldFactory.getAttachmentUploadField();
    }

    @Override
    public List<Component> getComponents() {
        return editFormFieldFactory.getComponentSelect().getSelectedItems();
    }

    @Override
    public List<Version> getAffectedVersions() {
        return editFormFieldFactory.getAffectedVersionSelect().getSelectedItems();
    }

    @Override
    public List<Version> getFixedVersion() {
        return editFormFieldFactory.getFixedVersionSelect().getSelectedItems();
    }

    @Override
    public HasEditFormHandlers<SimpleBug> getEditFormHandlers() {
        return this.editForm;
    }

    @Override
    protected String initFormHeader() {
        return (beanItem.getId() == null) ? UserUIContext.getMessage(BugI18nEnum.NEW) : UserUIContext.getMessage(BugI18nEnum.DETAIL);
    }

    @Override
    protected String initFormTitle() {
        return (beanItem.getId() == null) ? null : beanItem.getName();
    }

    @Override
    protected VaadinIcons initFormIconResource() {
        return ProjectAssetsManager.getAsset(ProjectTypeConstants.BUG);
    }

    @Override
    protected ComponentContainer createButtonControls() {
        return generateEditFormControls(editForm);
    }

    @Override
    protected AdvancedEditBeanForm<SimpleBug> initPreviewForm() {
        return new AdvancedEditBeanForm<>();
    }

    @Override
    protected IFormLayoutFactory initFormLayoutFactory() {
        if (beanItem.getId() == null) {
            return new DefaultDynaFormLayout(ProjectTypeConstants.BUG, BugDefaultFormLayoutFactory.getAddForm());
        } else {
            return new DefaultDynaFormLayout(ProjectTypeConstants.BUG, BugDefaultFormLayoutFactory.getAddForm(), "selected");
        }
    }

    @Override
    protected AbstractBeanFieldGroupEditFieldFactory<SimpleBug> initBeanFormFieldFactory() {
        editFormFieldFactory = new BugEditFormFieldFactory(editForm, CurrentProjectVariables.getProjectId());
        return editFormFieldFactory;
    }

    @Override
    public List<String> getFollowers() {
        return editFormFieldFactory.getSubscribersComp().getFollowers();
    }
}
