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
package com.airback.vaadin.web.ui;

import com.hp.gagawa.java.elements.Img;
import com.airback.common.domain.MailRecipientField;
import com.airback.core.utils.StringUtils;
import com.airback.db.arguments.NumberSearchField;
import com.airback.module.file.StorageUtils;
import com.airback.module.user.domain.SimpleUser;
import com.airback.module.user.domain.criteria.UserSearchCriteria;
import com.airback.module.user.service.UserService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import org.vaadin.viritin.button.MButton;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author airback Ltd.
 * @since 1.0
 */
// TODO
class EmailTokenField extends CssLayout  {
    private static final long serialVersionUID = 1L;

    private Set<String> inviteEmails;
//    private SuggestField suggestField;
    private List<SimpleUser> candidateUsers;
    private String lastQuery = "";
    private boolean isFocusing = false;

    EmailTokenField() {
        inviteEmails = new HashSet<>();
        this.setWidth("100%");
        this.addStyleName("member-token");
//        new Restrain(this).setMinHeight("50px");
//        suggestField = new SuggestField();
//        suggestField.setHeight("25px");
//        suggestField.setWidth("300px");
//        suggestField.setNewItemsAllowed(true);
//        suggestField.setNewItemsHandler(this);
//        suggestField.focus();
//        suggestField.setImmediate(true);
//        suggestField.setTokenMode(true);
//        suggestField.setSuggestionHandler(this);
//        suggestField.setSuggestionConverter(new UserSuggestionConverter());
//        suggestField.setTokenHandler(this);
//        suggestField.setMinimumQueryCharacters(1);
//        suggestField.setPopupWidth(400);
//
//        addComponent(suggestField);
        UserService userService = AppContextUtil.getSpringBean(UserService.class);
        UserSearchCriteria searchCriteria = new UserSearchCriteria();
        searchCriteria.setSaccountid(NumberSearchField.equal(AppUI.getAccountId()));
        candidateUsers = (List<SimpleUser>) userService.findAbsoluteListByCriteria(searchCriteria, 0, Integer.MAX_VALUE);
//        suggestField.addBlurListener(blurEvent -> {
//                    isFocusing = false;
//                    if (!"".equals(lastQuery) && StringUtils.isValidEmail(lastQuery) && !inviteEmails.contains(lastQuery)) {
//                        handleToken(lastQuery);
//                    }
//                }
//        );
//
//        suggestField.addFocusListener(focusEvent -> {
//                    isFocusing = true;
//                    lastQuery = "";
//                }
//        );
//        this.addLayoutClickListener(layoutClickEvent -> {
//            if (layoutClickEvent.getClickedComponent() == null) {
//                suggestField.focus();
//            }
//        });
    }

//    @Override
//    public Object addNewItem(String value) {
//        lastQuery = "";
//        if (StringUtils.isValidEmail(value) && !inviteEmails.contains(value)) {
//            return value;
//        } else {
//            NotificationUtil.showWarningNotification(value + " is not a valid email or it is already in the list");
//        }
//        return null;
//    }

    public List<MailRecipientField> getListRecipients() {
        if (!"".equals(lastQuery) && StringUtils.isValidEmail(lastQuery) && !inviteEmails.contains(lastQuery)) {
            inviteEmails.add(lastQuery);
        }
        return inviteEmails.stream().map(mail -> new MailRecipientField(mail, mail))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<Object> searchItems(String query) {
//        if (StringUtils.isBlank(query) || !isFocusing) {
//            return Collections.emptyList();
//        }
//        lastQuery = query;
//        List<SimpleUser> result = new ArrayList<>();
//        for (SimpleUser user : candidateUsers) {
//            if (user.getEmail().contains(query) || user.getDisplayName().contains(query)) {
//                result.add(user);
//            }
//        }
//        return new ArrayList<>(result);
//    }
//
//    @Override
//    public void handleToken(Object token) {
//        if (token != null) {
//            if (token instanceof String) {
//                String address = (String) token;
//                if (!inviteEmails.contains(address)) {
//                    addToken(generateToken(address));
//                    inviteEmails.add(address);
//                }
//            } else if (token instanceof SimpleUser) {
//                SimpleUser user = (SimpleUser) token;
//                if (!inviteEmails.contains(user.getEmail())) {
//                    addToken(generateToken(user));
//                    inviteEmails.add(user.getEmail());
//                } else {
//                    NotificationUtil.showWarningNotification("Email " + user.getEmail() + " is already in the list");
//                }
//            } else {
//                throw new airbackException("Do not support token type " + token);
//            }
//            lastQuery = "";
//        }
//    }
//
//    private void addToken(Component button) {
//        int index = getComponentIndex(suggestField);
//        addComponent(button, index);
//    }

    private Component generateToken(final String email) {
        MButton btn = new MButton(email).withIcon(VaadinIcons.CLOSE_SMALL).withStyleName("token-field");
        btn.addClickListener(clickEvent -> {
            EmailTokenField.this.removeComponent(btn);
            inviteEmails.remove(email);
        });
        return btn;
    }

    private Component generateToken(final SimpleUser user) {
        final Button btn = new Button("", VaadinIcons.CLOSE_SMALL);
        btn.setCaptionAsHtml(true);
        btn.setCaption((new Img("", StorageUtils.getAvatarPath(user.getAvatarid(), 16))).write() + " " + user.getDisplayName());
        btn.addClickListener(clickEvent -> {
            EmailTokenField.this.removeComponent(btn);
            inviteEmails.remove(user.getEmail());
        });
        btn.setStyleName("token-field");
        return btn;
    }

    private class UserSuggestionConverter {
        private UserSuggestionConverter() {
//            super(SimpleUser.class, "email", "displayName", "email");
        }

//        @Override
//        public Object toItem(SuggestFieldSuggestion suggestion) {
//            SimpleUser result = null;
//            for (SimpleUser bean : candidateUsers) {
//                if (bean.getEmail().equals(suggestion.getId())) {
//                    result = bean;
//                    break;
//                }
//            }
//            assert result != null : "This should not be happening";
//            return result;
//        }
    }
}