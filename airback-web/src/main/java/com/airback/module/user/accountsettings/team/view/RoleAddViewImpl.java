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
import com.airback.module.user.accountsettings.localization.RoleI18nEnum;
import com.airback.module.user.domain.Role;
import com.airback.module.user.domain.SimpleRole;
import com.airback.module.user.view.component.PermissionComboBoxFactory;
import com.airback.module.user.view.component.YesNoPermissionComboBox;
import com.airback.security.PermissionDefItem;
import com.airback.security.PermissionMap;
import com.airback.security.RolePermissionCollections;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasEditFormHandlers;
import com.airback.vaadin.mvp.AbstractVerticalPageView;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.ui.FormContainer;
import com.airback.vaadin.ui.GenericBeanForm;
import com.airback.vaadin.web.ui.AddViewLayout;
import com.airback.vaadin.web.ui.KeyCaptionComboBox;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.airback.vaadin.web.ui.utils.FormControlsGenerator.generateEditFormControls;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class RoleAddViewImpl extends AbstractVerticalPageView implements RoleAddView {
    private static final long serialVersionUID = 1L;

    private EditForm editForm;
    private Role role;

    public RoleAddViewImpl() {
        this.setMargin(new MarginInfo(false, true, true, true));
        this.editForm = new EditForm();
        this.addComponent(this.editForm);
    }

    @Override
    public void editItem(Role item) {
        this.role = item;
        this.editForm.setBean(this.role);
    }

    @Override
    public PermissionMap getPermissionMap() {
        return editForm.getPermissionMap();
    }

    public class EditForm extends AdvancedEditBeanForm<Role> {
        private static final long serialVersionUID = 1L;
        private final Map<String, KeyCaptionComboBox> permissionControlsMap = new HashMap<>();

        @Override
        public void setBean(Role item) {
            this.setFormLayoutFactory(new EditForm.FormLayoutFactory());
            this.setBeanFormFieldFactory(new EditFormFieldFactory(EditForm.this));
            super.setBean(item);
        }

        private class FormLayoutFactory extends RoleFormLayoutFactory {

            FormLayoutFactory() {
                super("");
            }

            @Override
            public AbstractComponent getLayout() {
                AddViewLayout formAddLayout = new AddViewLayout(initFormHeader(), VaadinIcons.USERS);

                ComponentContainer topLayout = createButtonControls();
                if (topLayout != null) {
                    formAddLayout.addHeaderRight(topLayout);
                }
                formAddLayout.setTitle(initFormTitle());

                wrappedLayoutFactory = new RoleInformationLayout();
                formAddLayout.addBody(wrappedLayoutFactory.getLayout());

                ComponentContainer bottomPanel = createBottomPanel();
                if (bottomPanel != null) {
                    formAddLayout.addBottom(bottomPanel);
                }

                return formAddLayout;
            }

            protected String initFormHeader() {
                return role.getId() == null ? UserUIContext.getMessage(RoleI18nEnum.NEW) : UserUIContext.getMessage(RoleI18nEnum.DETAIL);
            }

            protected String initFormTitle() {
                return role.getId() == null ? null : role.getRolename();
            }

            private ComponentContainer createButtonControls() {
                return generateEditFormControls(EditForm.this);
            }

            @Override
            protected Layout createBottomPanel() {
                 MVerticalLayout permissionsPanel = new MVerticalLayout().withMargin(false);

                PermissionMap perMap;
                if (role instanceof SimpleRole) {
                    perMap = ((SimpleRole) role).getPermissionMap();
                } else {
                    perMap = new PermissionMap();
                }

                permissionsPanel.addComponent(constructGridLayout(UserUIContext.getMessage(RoleI18nEnum.SECTION_PROJECT_MANAGEMENT_TITLE),
                        perMap, RolePermissionCollections.PROJECT_PERMISSION_ARR));

                permissionsPanel.addComponent(constructGridLayout(UserUIContext.getMessage(RoleI18nEnum.SECTION_ACCOUNT_MANAGEMENT_TITLE),
                        perMap, RolePermissionCollections.ACCOUNT_PERMISSION_ARR));

                return permissionsPanel;
            }
        }

        private ComponentContainer constructGridLayout(String depotTitle, PermissionMap perMap, List<PermissionDefItem> defItems) {
            GridFormLayoutHelper formHelper = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.TWO_COLUMN);
            FormContainer permissionsPanel = new FormContainer();
            permissionsPanel.addSection(depotTitle, formHelper.getLayout());

            for (int i = 0; i < defItems.size(); i++) {
                PermissionDefItem permissionDefItem = defItems.get(i);
                KeyCaptionComboBox permissionBox = PermissionComboBoxFactory.createPermissionSelection(permissionDefItem.getPermissionCls());
                Integer flag = perMap.getPermissionFlag(permissionDefItem.getKey());
                permissionBox.setValue(KeyCaptionComboBox.Entry.of(flag));
                Enum captionHelp;
                if (permissionBox instanceof YesNoPermissionComboBox) {
                    captionHelp = SecurityI18nEnum.BOOLEAN_PERMISSION_HELP;
                } else {
                    captionHelp = SecurityI18nEnum.ACCESS_PERMISSION_HELP;
                }
                permissionControlsMap.put(permissionDefItem.getKey(), permissionBox);
                formHelper.addComponent(permissionBox, UserUIContext.getMessage(permissionDefItem.getCaption()),
                        UserUIContext.getMessage(captionHelp), i % 2, i / 2);
            }

            return permissionsPanel;
        }

        protected PermissionMap getPermissionMap() {
            PermissionMap permissionMap = new PermissionMap();

            for (Map.Entry<String, KeyCaptionComboBox> entry : permissionControlsMap.entrySet()) {
                KeyCaptionComboBox permissionBox = entry.getValue();
                KeyCaptionComboBox.Entry perValue = permissionBox.getValue();
                permissionMap.addPath(entry.getKey(), perValue.getKey());
            }
            return permissionMap;
        }

        private class EditFormFieldFactory extends AbstractBeanFieldGroupEditFieldFactory<Role> {
            private static final long serialVersionUID = 1L;

            EditFormFieldFactory(GenericBeanForm<Role> form) {
                super(form);
            }

            @Override
            protected HasValue<?> onCreateField(final Object propertyId) {
                if (Role.Field.description.equalTo(propertyId)) {
                    return new RichTextArea();
                } else if (Role.Field.rolename.equalTo(propertyId)) {
                    return new MTextField().withRequiredIndicatorVisible(true);
                } else if (Role.Field.isdefault.equalTo(propertyId)) {
                    return new CheckBox();
                }
                return null;
            }
        }
    }

    @Override
    public HasEditFormHandlers<Role> getEditFormHandlers() {
        return this.editForm;
    }
}
