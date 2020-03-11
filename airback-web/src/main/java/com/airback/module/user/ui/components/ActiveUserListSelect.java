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
package com.airback.module.user.ui.components;

import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.billing.RegisterStatusConstants;
import com.airback.module.user.domain.SimpleUser;
import com.airback.module.user.domain.criteria.UserSearchCriteria;
import com.airback.module.user.service.UserService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.ui.UserAvatarControlFactory;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.ListSelect;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class ActiveUserListSelect extends ListSelect<SimpleUser> {
    private static final long serialVersionUID = 1L;

    public ActiveUserListSelect() {

        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
        criteria.setRegisterStatuses(new SetSearchField<>(RegisterStatusConstants.ACTIVE));

        UserService userService = AppContextUtil.getSpringBean(UserService.class);
        List<SimpleUser> users = (List<SimpleUser>) userService.findPageableListByCriteria(new BasicSearchRequest<>(criteria));

        this.setItems(users);
        this.setItemCaptionGenerator((ItemCaptionGenerator<SimpleUser>) user -> user.getDisplayName());
        this.setItemIconGenerator((IconGenerator<SimpleUser>) user -> UserAvatarControlFactory.createAvatarResource(user.getAvatarid(), 16));

        this.setRows(4);
    }
}
