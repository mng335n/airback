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
package com.airback.module.project.view.milestone;

import com.airback.common.i18n.OptionI18nEnum.StatusI18nEnum;
import com.airback.core.utils.DateTimeUtils;
import com.airback.db.arguments.DateSearchField;
import com.airback.db.arguments.NumberSearchField;
import com.airback.db.arguments.SearchField;
import com.airback.db.arguments.SetSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.module.project.domain.Milestone;
import com.airback.module.project.domain.ProjectTicket;
import com.airback.module.project.domain.SimpleMilestone;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.i18n.BugI18nEnum;
import com.airback.module.project.i18n.OptionI18nEnum.MilestoneStatus;
import com.airback.module.project.i18n.RiskI18nEnum;
import com.airback.module.project.i18n.TaskI18nEnum;
import com.airback.module.project.service.ProjectTicketService;
import com.airback.module.project.ui.ProjectAssetsUtil;
import com.airback.module.project.ui.form.ProjectFormAttachmentDisplayField;
import com.airback.module.project.view.ticket.TicketRowRenderer;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.AbstractBeanFieldGroupViewFieldFactory;
import com.airback.vaadin.ui.GenericBeanForm;
import com.airback.vaadin.ui.field.RichTextViewField;
import com.airback.vaadin.ui.field.StyleViewField;
import com.airback.vaadin.web.ui.AdvancedPreviewBeanForm;
import com.airback.vaadin.web.ui.DefaultBeanPagedList;
import com.airback.vaadin.web.ui.DefaultDynaFormLayout;
import com.airback.vaadin.web.ui.WebThemes;
import com.airback.vaadin.web.ui.field.ContainerViewField;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * @author airback Ltd
 * @since 5.2.10
 */
public class MilestonePreviewForm extends AdvancedPreviewBeanForm<SimpleMilestone> {
    @Override
    public void setBean(SimpleMilestone bean) {
        this.setFormLayoutFactory(new DefaultDynaFormLayout(ProjectTypeConstants.MILESTONE, MilestoneDefaultFormLayoutFactory.getReadForm(),
                Milestone.Field.name.name()));
        this.setBeanFormFieldFactory(new MilestoneFormFieldFactory(this));
        super.setBean(bean);
    }

    private static class MilestoneFormFieldFactory extends AbstractBeanFieldGroupViewFieldFactory<SimpleMilestone> {
        private static final long serialVersionUID = 1L;

        MilestoneFormFieldFactory(GenericBeanForm<SimpleMilestone> form) {
            super(form);
        }

        @Override
        protected HasValue<?> onCreateField(final Object propertyId) {
            SimpleMilestone milestone = attachForm.getBean();
            if (Milestone.Field.description.equalTo(propertyId)) {
                return new RichTextViewField();
            } else if (Milestone.Field.status.equalTo(propertyId)) {
                String milestoneStatus = UserUIContext.getMessage(MilestoneStatus.class, milestone.getStatus());
                VaadinIcons statusIcon = ProjectAssetsUtil.getPhaseIcon(milestone.getStatus());
                return new StyleViewField(statusIcon.getHtml() + " " + milestoneStatus)
                        .withStyleName(WebThemes.FIELD_NOTE);
            } else if ("section-assignments".equals(propertyId)) {
                ContainerViewField containerField = new ContainerViewField();
                containerField.addComponentField(new AssignmentsComp(milestone));
                return containerField;
            } else if ("section-attachments".equals(propertyId)) {
                return new ProjectFormAttachmentDisplayField(milestone.getProjectid(), ProjectTypeConstants.MILESTONE,
                        milestone.getId());
            }
            return null;
        }
    }

    private static class AssignmentsComp extends MVerticalLayout {
        private ProjectTicketSearchCriteria searchCriteria;
        private SimpleMilestone beanItem;
        private DefaultBeanPagedList<ProjectTicketService, ProjectTicketSearchCriteria, ProjectTicket> assignmentsLayout;

        AssignmentsComp(SimpleMilestone milestone) {
            this.beanItem = milestone;
            withMargin(false).withFullWidth().withStyleName(WebThemes.NO_SCROLLABLE_CONTAINER);
            MHorizontalLayout header = new MHorizontalLayout().withMargin(new MarginInfo(false, false, true, false)).withFullWidth();

            CheckBox openSelection = new CheckBox(UserUIContext.getMessage(StatusI18nEnum.Open), true);
            openSelection.addValueChangeListener(valueChangeEvent -> {
                if (openSelection.getValue()) {
                    searchCriteria.setOpen(new SearchField());
                } else {
                    searchCriteria.setOpen(null);
                }
                updateSearchStatus();
            });

            CheckBox overdueSelection = new CheckBox(UserUIContext.getMessage(StatusI18nEnum.Overdue), false);
            overdueSelection.addValueChangeListener(valueChangeEvent -> {
                if (overdueSelection.getValue()) {
                    searchCriteria.setDueDate(new DateSearchField(DateTimeUtils.getCurrentDateWithoutMS().toLocalDate(),
                            DateSearchField.LESS_THAN));
                } else {
                    searchCriteria.setDueDate(null);
                }
                updateSearchStatus();
            });

            Label spacingLbl1 = new Label("");

            CheckBox taskSelection = new CheckBox(UserUIContext.getMessage(TaskI18nEnum.LIST), true);
            taskSelection.addValueChangeListener(valueChangeEvent -> updateTypeSearchStatus(taskSelection.getValue(),
                    ProjectTypeConstants.TASK));

            CheckBox bugSelection = new CheckBox(UserUIContext.getMessage(BugI18nEnum.LIST), true);
            bugSelection.addValueChangeListener(valueChangeEvent -> updateTypeSearchStatus(bugSelection.getValue(),
                    ProjectTypeConstants.BUG));

            CheckBox riskSelection = new CheckBox(UserUIContext.getMessage(RiskI18nEnum.LIST), true);
            riskSelection.addValueChangeListener(valueChangeEvent -> updateTypeSearchStatus(riskSelection.getValue(),
                    ProjectTypeConstants.RISK));

            header.with(openSelection, overdueSelection, spacingLbl1, taskSelection, bugSelection, riskSelection)
                    .withAlign(openSelection, Alignment.MIDDLE_LEFT).withAlign(overdueSelection, Alignment.MIDDLE_LEFT)
                    .withAlign(taskSelection, Alignment.MIDDLE_LEFT).withAlign(bugSelection, Alignment.MIDDLE_LEFT)
                    .withAlign(riskSelection, Alignment.MIDDLE_LEFT).expand(spacingLbl1);

            assignmentsLayout = new DefaultBeanPagedList<>(AppContextUtil.getSpringBean(ProjectTicketService.class), new TicketRowRenderer());
            this.with(header, assignmentsLayout);
            searchCriteria = new ProjectTicketSearchCriteria();
            searchCriteria.setProjectIds(new SetSearchField<>(CurrentProjectVariables.getProjectId()));
            searchCriteria.setOpen(new SearchField());
            searchCriteria.setTypes(new SetSearchField<>(ProjectTypeConstants.BUG, ProjectTypeConstants.TASK, ProjectTypeConstants.RISK));
            searchCriteria.setMilestoneId(new NumberSearchField(beanItem.getId()));
            updateSearchStatus();
        }

        private void updateTypeSearchStatus(boolean selection, String type) {
            SetSearchField<String> types = searchCriteria.getTypes();
            if (types == null) {
                types = new SetSearchField<>();
            }
            if (selection) {
                types.addValue(type);
            } else {
                types.removeValue(type);
            }
            searchCriteria.setTypes(types);
            updateSearchStatus();
        }

        private void updateSearchStatus() {
            assignmentsLayout.setSearchCriteria(searchCriteria);
        }
    }

}
