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
package com.airback.module.project.view.task;

import com.airback.common.domain.CommentWithBLOBs;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.common.service.CommentService;
import com.airback.core.utils.StringUtils;
import com.airback.form.view.LayoutType;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.Task;
import com.airback.module.project.event.TaskEvent;
import com.airback.module.project.i18n.TaskI18nEnum;
import com.airback.module.project.service.TaskService;
import com.airback.module.project.view.settings.component.ProjectMemberSelectionField;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.airback.vaadin.ui.AbstractFormLayoutFactory;
import com.airback.vaadin.ui.AdvancedEditBeanForm;
import com.airback.vaadin.ui.GenericBeanForm;
import com.airback.vaadin.web.ui.WebThemes;
import com.airback.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;
import org.vaadin.viritin.layouts.MWindow;

import java.time.LocalDateTime;

/**
 * @author airback Ltd.
 * @since 1.0
 */
class AssignTaskWindow extends MWindow {
    private static final long serialVersionUID = 1L;
    private final Task task;

    AssignTaskWindow(Task task) {
        super(UserUIContext.getMessage(TaskI18nEnum.DIALOG_ASSIGN_TASK_TITLE, task.getName()));
        this.task = task;
        MVerticalLayout contentLayout = new MVerticalLayout().withMargin(new MarginInfo(false, false, true, false));
        this.withWidth("750px").withModal(true).withResizable(false).withCenter().withContent(contentLayout);
        EditForm editForm = new EditForm();
        contentLayout.addComponent(editForm);
        editForm.setBean(task);
    }

    private class EditForm extends AdvancedEditBeanForm<Task> {
        private static final long serialVersionUID = 1L;
        private RichTextArea commentArea;

        @Override
        public void setBean(Task newDataSource) {
            this.setFormLayoutFactory(new FormLayoutFactory());
            this.setBeanFormFieldFactory(new EditFormFieldFactory(EditForm.this));
            super.setBean(newDataSource);
        }

        class FormLayoutFactory extends AbstractFormLayoutFactory {
            private GridFormLayoutHelper informationLayout;

            @Override
            public AbstractComponent getLayout() {
                VerticalLayout layout = new VerticalLayout();
                this.informationLayout = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.ONE_COLUMN);
                layout.addComponent(informationLayout.getLayout());

                MButton cancelBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_CANCEL), clickEvent -> close())
                        .withStyleName(WebThemes.BUTTON_OPTION);

                MButton approveBtn = new MButton(UserUIContext.getMessage(GenericI18Enum.BUTTON_ASSIGN), clickEvent -> {
                    if (EditForm.this.validateForm()) {
                        // Save task status and assignee
                        TaskService taskService = AppContextUtil.getSpringBean(TaskService.class);
                        taskService.updateWithSession(task, UserUIContext.getUsername());

                        // Save comment
                        String commentValue = commentArea.getValue();
                        if (StringUtils.isNotBlank(commentValue)) {
                            CommentWithBLOBs comment = new CommentWithBLOBs();
                            comment.setComment(commentArea.getValue());
                            comment.setCreatedtime(LocalDateTime.now());
                            comment.setCreateduser(UserUIContext.getUsername());
                            comment.setSaccountid(AppUI.getAccountId());
                            comment.setType(ProjectTypeConstants.TASK);
                            comment.setTypeid("" + task.getId());
                            comment.setExtratypeid(CurrentProjectVariables.getProjectId());

                            CommentService commentService = AppContextUtil.getSpringBean(CommentService.class);
                            commentService.saveWithSession(comment, UserUIContext.getUsername());
                        }

                        close();
                        EventBusFactory.getInstance().post(new TaskEvent.GotoRead(this, task.getId()));
                    }
                }).withIcon(VaadinIcons.SHARE).withStyleName(WebThemes.BUTTON_ACTION);

                MHorizontalLayout controlsBtn = new MHorizontalLayout(cancelBtn, approveBtn).withMargin(false);
                layout.addComponent(controlsBtn);
                layout.setComponentAlignment(controlsBtn, Alignment.MIDDLE_RIGHT);
                return layout;
            }

            @Override
            protected HasValue<?> onAttachField(Object propertyId, HasValue<?> field) {
                if (Task.Field.assignuser.equalTo(propertyId)) {
                    return informationLayout.addComponent(field, UserUIContext.getMessage(GenericI18Enum.FORM_ASSIGNEE), 0, 0);
                } else if (propertyId.equals("comment")) {
                    return informationLayout.addComponent(field, UserUIContext.getMessage(GenericI18Enum.OPT_COMMENT), 0, 1);
                }
                return null;
            }
        }

        private class EditFormFieldFactory extends AbstractBeanFieldGroupEditFieldFactory<Task> {
            private static final long serialVersionUID = 1L;

            EditFormFieldFactory(GenericBeanForm<Task> form) {
                super(form);
            }

            @Override
            protected HasValue<?> onCreateField(Object propertyId) {
                if (propertyId.equals("assignuser")) {
                    return new ProjectMemberSelectionField();
                } else if (propertyId.equals("comment")) {
                    commentArea = new RichTextArea();
                    return commentArea;
                }
                return null;
            }
        }
    }
}
