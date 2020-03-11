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
package com.airback.module.project.view.page;

import com.airback.module.page.domain.Page;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.i18n.PageI18nEnum;
import com.airback.module.project.ui.ProjectAssetsManager;
import com.airback.module.project.ui.components.AbstractEditItemComp;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.ui.IFormLayoutFactory;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComponentContainer;

import static com.airback.vaadin.web.ui.utils.FormControlsGenerator.generateEditFormControls;

/**
 * @author airback Ltd.
 * @since 4.4.0
 */
@ViewComponent
public class PageAddViewImpl extends AbstractEditItemComp<Page> implements PageAddView {
    private static final long serialVersionUID = 1L;

    @Override
    protected String initFormHeader() {
        return (beanItem.isNew()) ? UserUIContext.getMessage(PageI18nEnum.NEW) : UserUIContext.getMessage(PageI18nEnum.DETAIL);
    }

    @Override
    protected String initFormTitle() {
        return (beanItem.isNew()) ? null : beanItem.getSubject();
    }

    @Override
    protected VaadinIcons initFormIconResource() {
        return ProjectAssetsManager.getAsset(ProjectTypeConstants.PAGE);
    }

    @Override
    protected ComponentContainer createButtonControls() {
        return generateEditFormControls(editForm);
    }

    @Override
    protected AdvancedEditBeanForm<Page> initPreviewForm() {
        return new AdvancedEditBeanForm<>();
    }

    @Override
    protected IFormLayoutFactory initFormLayoutFactory() {
        return new PageFormLayoutFactory();
    }

    @Override
    protected AbstractBeanFieldGroupEditFieldFactory<Page> initBeanFormFieldFactory() {
        return new PageEditFormFieldFactory(editForm);
    }
}
