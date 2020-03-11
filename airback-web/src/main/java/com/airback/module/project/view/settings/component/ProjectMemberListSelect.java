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
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.ProjectMemberStatusConstants;
import com.airback.module.project.domain.SimpleProjectMember;
import com.airback.module.project.domain.criteria.ProjectMemberSearchCriteria;
import com.airback.module.project.service.ProjectMemberService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.ui.UserAvatarControlFactory;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.ListSelect;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 4.0
 */
public class ProjectMemberListSelect extends ListSelect<SimpleProjectMember> {
    private static final long serialVersionUID = 1L;

    public ProjectMemberListSelect(List<Integer> projectIds) {
        this(true, projectIds);
    }

    public ProjectMemberListSelect(boolean activeMembers, List<Integer> projectIds) {
        ProjectMemberSearchCriteria criteria = new ProjectMemberSearchCriteria();
        criteria.setProjectIds(new SetSearchField<>(projectIds));

        if (activeMembers) {
            criteria.setStatuses(new SetSearchField<>(ProjectMemberStatusConstants.ACTIVE));
        }

        ProjectMemberService projectMemberService = AppContextUtil.getSpringBean(ProjectMemberService.class);
        List<SimpleProjectMember> members = (List<SimpleProjectMember>) projectMemberService.findPageableListByCriteria(new BasicSearchRequest<>(criteria));
        this.setItems(members);
        this.setItemCaptionGenerator((ItemCaptionGenerator<SimpleProjectMember>) SimpleProjectMember::getDisplayName);
        setItemIconGenerator((IconGenerator<SimpleProjectMember>) member -> UserAvatarControlFactory.createAvatarResource(member.getMemberAvatarId(), 16));
        this.setRows(4);
    }
}
