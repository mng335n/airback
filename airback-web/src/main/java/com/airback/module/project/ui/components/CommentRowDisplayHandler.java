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

import com.airback.common.domain.SimpleComment;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.common.service.CommentService;
import com.airback.module.ecm.domain.Content;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.*;
import com.airback.vaadin.web.ui.AttachmentDisplayComponent;
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.apache.commons.collections.CollectionUtils;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class CommentRowDisplayHandler implements IBeanList.RowDisplayHandler<SimpleComment> {

    @Override
    public Component generateRow(IBeanList<SimpleComment> host, final SimpleComment comment, int rowIndex) {
        final MHorizontalLayout layout = new MHorizontalLayout().withMargin(new MarginInfo(true, true, true, false))
                .withFullWidth();

        ProjectMemberBlock memberBlock = new ProjectMemberBlock(comment.getCreateduser(), comment.getOwnerAvatarId(), comment.getOwnerFullName());
        layout.addComponent(memberBlock);

        MVerticalLayout rowLayout = new MVerticalLayout().withFullWidth().withStyleName(WebThemes.MESSAGE_CONTAINER);

        MHorizontalLayout messageHeader = new MHorizontalLayout().withMargin(false).withFullWidth();
        messageHeader.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        ELabel timePostLbl = ELabel.html(UserUIContext.getMessage(GenericI18Enum.EXT_ADDED_COMMENT, comment.getOwnerFullName(),
                UserUIContext.formatPrettyTime(comment.getCreatedtime())))
                .withDescription(UserUIContext.formatDateTime(comment.getCreatedtime()));
        timePostLbl.setStyleName(WebThemes.META_INFO);

        if (hasDeletePermission(comment)) {
            MButton msgDeleteBtn = new MButton(VaadinIcons.TRASH).withStyleName(WebThemes.BUTTON_ICON_ONLY)
                    .withListener(clickEvent -> ConfirmDialogExt.show(UI.getCurrent(),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                            UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                            UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                            confirmDialog -> {
                                if (confirmDialog.isConfirmed()) {
                                    CommentService commentService = AppContextUtil.getSpringBean(CommentService.class);
                                    commentService.removeWithSession(comment, UserUIContext.getUsername(), AppUI.getAccountId());
                                    ((BeanList) host).removeRow(layout);
                                }
                            }));
            messageHeader.with(timePostLbl, msgDeleteBtn).expand(timePostLbl);
        } else {
            messageHeader.with(timePostLbl).expand(timePostLbl);
        }

        rowLayout.addComponent(messageHeader);

        Label messageContent = new SafeHtmlLabel(comment.getComment());
        rowLayout.addComponent(messageContent);

        List<Content> attachments = comment.getAttachments();
        if (!CollectionUtils.isEmpty(attachments)) {
            MVerticalLayout messageFooter = new MVerticalLayout().withSpacing(false).withFullWidth();
            AttachmentDisplayComponent attachmentDisplay = new AttachmentDisplayComponent(attachments);
            attachmentDisplay.setWidth("100%");
            messageFooter.with(attachmentDisplay).withAlign(attachmentDisplay, Alignment.MIDDLE_RIGHT);
            rowLayout.addComponent(messageFooter);
        }

        layout.with(rowLayout).expand(rowLayout);
        return layout;
    }

    private boolean hasDeletePermission(SimpleComment comment) {
        return (UserUIContext.getUsername().equals(comment.getCreateduser()) || UserUIContext.isAdmin());
    }
}
