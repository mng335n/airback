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
package com.airback.module.project.ui.components;

import com.airback.core.SecureAccessException;
import com.airback.module.project.domain.Project;
import com.airback.module.project.domain.SimpleProject;
import com.airback.module.project.service.ProjectService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ItemCaptionGenerator;

import java.util.List;
import java.util.Optional;

/**
 * @author airback Ltd
 * @since 6.0.0
 */
public class UserProjectComboBox extends ComboBox<SimpleProject> {
    private List<SimpleProject> projects;

    public UserProjectComboBox() {
        ProjectService projectService = AppContextUtil.getSpringBean(ProjectService.class);
        if (UserUIContext.isAdmin()) {
            projects = projectService.getProjectsUserInvolved(null, AppUI.getAccountId());
        } else {
            projects = projectService.getProjectsUserInvolved(UserUIContext.getUsername(), AppUI.getAccountId());
        }

        setItems(projects);
        setItemCaptionGenerator((ItemCaptionGenerator<SimpleProject>) Project::getName);
        this.setWidth(WebThemes.FORM_CONTROL_WIDTH);
    }

    public SimpleProject setSelectedProjectById(Integer projectId) {
        if (projects.size() == 0) {
            throw new SecureAccessException();
        }

        SimpleProject result;
        if (projectId == null) {
            result = projects.get(0);
        } else {
            Optional<SimpleProject> canPro = projects.stream().filter(project -> project.getId() == projectId).findFirst();
            result = canPro.orElse(projects.get(0));
        }
        setValue(result);
        return result;
    }
}
