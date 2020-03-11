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
package com.airback.module.project.view.settings.component;

import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.NumberSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.ProjectRole;
import com.airback.module.project.domain.SimpleProjectRole;
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria;
import com.airback.module.project.i18n.ProjectRoleI18nEnum;
import com.airback.module.project.service.ProjectRoleService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ProjectRoleComboBox extends ComboBox<SimpleProjectRole> implements Converter<SimpleProjectRole, Integer> {
    private static final long serialVersionUID = 1L;

    private List<SimpleProjectRole> roles;

    public ProjectRoleComboBox() {
        setWidth(WebThemes.FORM_CONTROL_WIDTH);
        setEmptySelectionAllowed(false);
        ProjectRoleSearchCriteria criteria = new ProjectRoleSearchCriteria();
        criteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
        criteria.setProjectId(new NumberSearchField(CurrentProjectVariables.getProjectId()));

        ProjectRoleService roleService = AppContextUtil.getSpringBean(ProjectRoleService.class);
        roles = (List<SimpleProjectRole>) roleService.findPageableListByCriteria(new BasicSearchRequest<>(criteria));

        this.setItems(roles);

        this.setEmptySelectionAllowed(false);
        this.setItemCaptionGenerator((ItemCaptionGenerator<SimpleProjectRole>) ProjectRole::getRolename);
    }

    public void setDefaultValue() {
        setSelectedItem(roles.get(0));
    }

    public void selectRoleById(Integer roleId) {
        SimpleProjectRole selectedRole = roles.stream().filter(role-> role.getId() == roleId).findFirst().orElse(null);
        this.setSelectedItem(selectedRole);
    }

    @Override
    public Result<Integer> convertToModel(SimpleProjectRole role, ValueContext valueContext) {
        return (role != null)? Result.ok(role.getId()) : Result.ok(null);
    }

    @Override
    public SimpleProjectRole convertToPresentation(Integer roleId, ValueContext valueContext) {
        return roles.stream().filter(role -> role.getId() == roleId).findFirst().orElse(null);
    }
}
