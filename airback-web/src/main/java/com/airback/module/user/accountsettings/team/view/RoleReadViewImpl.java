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
package com.airback.module.user.accountsettings.team.view;

import com.airback.common.i18n.SecurityI18nEnum;
import com.airback.form.view.LayoutType;
import com.airback.i18n.LocalizationHelper;
import com.airback.module.user.accountsettings.localization.RoleI18nEnum;
import com.airback.module.user.domain.Role;
import com.airback.module.user.domain.SimpleRole;
import com.airback.module.user.ui.components.PreviewFormControlsGenerator;
import com.airback.security.AccessPermissionFlag;
import com.airback.security.PermissionDefItem;
import com.airback.security.PermissionMap;
import com.airback.security.RolePermissionCollections;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasPreviewFormHandlers;
import com.airback.vaadin.mvp.AbstractVerticalPageView;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.AbstractBeanFieldGroupViewFieldFactory;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.ui.FormContainer;
import com.airback.vaadin.ui.field.DefaultViewField;
import com.airback.vaadin.web.ui.AdvancedPreviewBeanForm;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class RoleReadViewImpl extends AbstractVerticalPageView implements RoleReadView {
    private static final long serialVersionUID = 1L;

    private AdvancedPreviewBeanForm<Role> previewForm;
    private SimpleRole role;

    public RoleReadViewImpl() {
        this.setMargin(new MarginInfo(false, true, true, true));

        MHorizontalLayout header = new MHorizontalLayout().withMargin(new MarginInfo(true, false, true, false)).withFullWidth();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        ELabel headerText = ELabel.h2(VaadinIcons.USERS.getHtml() + " " + UserUIContext.getMessage(RoleI18nEnum.DETAIL));
        header.with(headerText).expand(headerText);
        this.addComponent(header);

        this.previewForm = new AdvancedPreviewBeanForm<>();
        this.addComponent(this.previewForm);

        Layout controlButtons = createTopPanel();
        if (controlButtons != null) {
            header.addComponent(controlButtons);
        }
    }

    private Layout createTopPanel() {
        PreviewFormControlsGenerator<Role> buttonControls = new PreviewFormControlsGenerator<>(previewForm);
        return buttonControls.createButtonControls(RolePermissionCollections.ACCOUNT_ROLE);
    }

    @Override
    public void previewItem(SimpleRole role) {
        this.role = role;
        this.previewForm.setFormLayoutFactory(new FormLayoutFactory());
        this.previewForm.setBeanFormFieldFactory(new AbstractBeanFieldGroupViewFieldFactory<Role>(previewForm) {
            private static final long serialVersionUID = 1L;

            @Override
            protected HasValue<?> onCreateField(Object propertyId) {
                if (Role.Field.isdefault.equalTo(propertyId)) {
                    Enum localizeYesNo = LocalizationHelper.localizeYesNo(role.getIsdefault());
                    return new DefaultViewField(UserUIContext.getMessage(localizeYesNo));
                }
                return null;
            }
        });
        this.previewForm.setBean(role);
    }

    @Override
    public HasPreviewFormHandlers<Role> getPreviewFormHandlers() {
        return this.previewForm;
    }


    private ComponentContainer constructPermissionSectionView(String depotTitle, PermissionMap permissionMap,
                                                              List<PermissionDefItem> defItems) {
        GridFormLayoutHelper formHelper = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.TWO_COLUMN);
        FormContainer permissionsPanel = new FormContainer();

        for (int i = 0; i < defItems.size(); i++) {
            PermissionDefItem permissionDefItem = defItems.get(i);
            final Integer perVal = permissionMap.get(permissionDefItem.getKey());
            SecurityI18nEnum enumVal = AccessPermissionFlag.toVal(perVal);
            formHelper.addComponent(new Label(UserUIContext.getMessage(enumVal)), UserUIContext.getMessage(permissionDefItem.getCaption()),
                    UserUIContext.getMessage(enumVal.desc()), i % 2, i / 2);
        }
        permissionsPanel.addSection(depotTitle, formHelper.getLayout());
        return permissionsPanel;
    }

    @Override
    public SimpleRole getItem() {
        return this.role;
    }

    class FormLayoutFactory extends RoleFormLayoutFactory {

        FormLayoutFactory() {
            super(role.getRolename());
        }

        @Override
        protected Layout createBottomPanel() {
            MVerticalLayout permissionsPanel = new MVerticalLayout().withMargin(false);

            PermissionMap permissionMap = role.getPermissionMap();

            permissionsPanel.addComponent(constructPermissionSectionView(UserUIContext.getMessage(RoleI18nEnum.SECTION_PROJECT_MANAGEMENT_TITLE),
                    permissionMap, RolePermissionCollections.PROJECT_PERMISSION_ARR));

            permissionsPanel.addComponent(constructPermissionSectionView(UserUIContext.getMessage(RoleI18nEnum.SECTION_ACCOUNT_MANAGEMENT_TITLE),
                    permissionMap, RolePermissionCollections.ACCOUNT_PERMISSION_ARR));

            return permissionsPanel;
        }
    }
}
