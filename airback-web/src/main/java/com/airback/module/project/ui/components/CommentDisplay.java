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
package com.airback.module.project.ui.components;

import com.airback.common.domain.SimpleComment;
import com.airback.common.domain.criteria.CommentSearchCriteria;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.common.service.CommentService;
import com.airback.db.arguments.StringSearchField;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.BeanList;
import com.airback.vaadin.ui.ReloadableComponent;
import com.airback.vaadin.web.ui.TabSheetLazyLoadComponent;
import com.vaadin.shared.ui.MarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public class CommentDisplay extends MVerticalLayout implements ReloadableComponent {
    private static final long serialVersionUID = 1L;

    private BeanList<CommentService, CommentSearchCriteria, SimpleComment> commentList;
    private String type;
    private String typeId;
    private ProjectCommentInput commentBox;

    public CommentDisplay(String type, Integer extraTypeId) {
        withMargin(new MarginInfo(true, false, true, false)).withFullWidth();
        this.type = type;
        commentBox = new ProjectCommentInput(this, type, extraTypeId);
        commentBox.setWidth("100%");
        this.addComponent(commentBox);

        commentList = new BeanList<>(AppContextUtil.getSpringBean(CommentService.class), new CommentRowDisplayHandler());
        commentList.setDisplayEmptyListText(false);
        this.addComponent(commentList);

        displayCommentList();
    }

    private void displayCommentList() {
        if (type == null || typeId == null) {
            return;
        }

        CommentSearchCriteria searchCriteria = new CommentSearchCriteria();
        searchCriteria.setType(StringSearchField.and(type));
        searchCriteria.setTypeId(StringSearchField.and(typeId));
        int numComments = commentList.setSearchCriteria(searchCriteria);

        Object parentComp = this.getParent();
        if (parentComp instanceof TabSheetLazyLoadComponent) {
            ((TabSheetLazyLoadComponent) parentComp).getTab(this).setCaption(UserUIContext.getMessage(GenericI18Enum.OPT_COMMENTS_VALUE, numComments));
        }
    }

    public void loadComments(String typeId) {
        this.typeId = typeId;
        if (commentBox != null) {
            commentBox.setTypeAndId(typeId);
        }
        displayCommentList();
    }

    @Override
    public void reload() {
        displayCommentList();
    }
}