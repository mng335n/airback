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

import com.airback.core.utils.StringUtils;
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
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.ItemCaptionGenerator;

import java.util.List;
import java.util.Optional;

/**
 * @author airback Ltd.
 * @since 2.0
 */
public class ActiveUserComboBox extends ComboBox<SimpleUser> implements Converter<SimpleUser, String> {
    private static final long serialVersionUID = 1L;

    private List<SimpleUser> users;

    public ActiveUserComboBox() {
        this.setWidth(WebThemes.FORM_CONTROL_WIDTH);
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setSaccountid(new NumberSearchField(AppUI.getAccountId()));
        criteria.setRegisterStatuses(new SetSearchField<>(RegisterStatusConstants.ACTIVE));

        UserService userService = AppContextUtil.getSpringBean(UserService.class);
        users = (List<SimpleUser>) userService.findPageableListByCriteria(new BasicSearchRequest<>(criteria));
        setItems(users);
        setItemCaptionGenerator((ItemCaptionGenerator<SimpleUser>) user -> StringUtils.trim(user.getDisplayName(), 30, true));
        setItemIconGenerator((IconGenerator<SimpleUser>) user -> UserAvatarControlFactory.createAvatarResource(user.getAvatarid(), 16));
    }

    public void selectUser(String username) {
        Optional<SimpleUser> selectedUser = users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
        setValue(selectedUser.orElse(null));
    }

    @Override
    public Result<String> convertToModel(SimpleUser value, ValueContext context) {
        return (value != null) ? Result.ok(value.getUsername()) : Result.ok(null);
    }

    @Override
    public SimpleUser convertToPresentation(String value, ValueContext context) {
        return users.stream().filter(user -> user.getUsername().equals(value)).findFirst().orElse(null);
    }
}
