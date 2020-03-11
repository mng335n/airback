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
package com.airback.module.project.view.ticket;

import com.google.common.eventbus.Subscribe;
import com.airback.common.domain.GroupItem;
import com.airback.core.utils.BeanUtility;
import com.airback.core.utils.StringUtils;
import com.airback.db.arguments.SearchField;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria;
import com.airback.module.project.event.TicketEvent;
import com.airback.module.project.i18n.TaskI18nEnum;
import com.airback.module.project.service.ProjectTicketService;
import com.airback.module.user.CommonTooltipGenerator;
import com.airback.module.user.domain.SimpleUser;
import com.airback.module.user.service.UserService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.ApplicationEventListener;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.UserAvatarControlFactory;
import com.airback.vaadin.web.ui.ProgressBarIndicator;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.apache.commons.collections.CollectionUtils;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 4.0
 */
public class UnresolvedTicketsByAssigneeWidget extends Panel {
    private static final long serialVersionUID = 1L;

    private ProjectTicketSearchCriteria searchCriteria;
    private int totalCountItems;
    private List<GroupItem> groupItems;

    private ApplicationEventListener<TicketEvent.HasTicketPropertyChanged> ticketPropertyChangedHandler = new
            ApplicationEventListener<TicketEvent.HasTicketPropertyChanged>() {
                @Override
                @Subscribe
                public void handle(TicketEvent.HasTicketPropertyChanged event) {
                    if (searchCriteria != null && ("assignUser".equals(event.getData()) || "all".equals(event.getData()))) {
                        UI.getCurrent().access(() -> setSearchCriteria(searchCriteria));
                    }
                }
            };

    public UnresolvedTicketsByAssigneeWidget() {
        super("", new MVerticalLayout());
    }

    @Override
    public void attach() {
        EventBusFactory.getInstance().register(ticketPropertyChangedHandler);
        super.attach();
    }

    @Override
    public void detach() {
        EventBusFactory.getInstance().unregister(ticketPropertyChangedHandler);
        super.detach();
    }

    public void setSearchCriteria(ProjectTicketSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;

        ProjectTicketService projectTicketService = AppContextUtil.getSpringBean(ProjectTicketService.class);
        totalCountItems = projectTicketService.getTotalCount(searchCriteria);
        groupItems = projectTicketService.getAssigneeSummary(searchCriteria);

        this.setCaption(String.format("%s (%d)", UserUIContext.getMessage(TaskI18nEnum.WIDGET_UNRESOLVED_BY_ASSIGNEE_TITLE), totalCountItems));
        displayPlainMode();
    }

    private void displayPlainMode() {
        MVerticalLayout bodyContent = (MVerticalLayout) getContent();
        bodyContent.removeAllComponents();
        int totalAssignTicketCounts = 0;
        if (CollectionUtils.isNotEmpty(groupItems)) {
            for (GroupItem item : groupItems) {
                MHorizontalLayout assigneeLayout = new MHorizontalLayout().withFullWidth();
                assigneeLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

                String assignUser = item.getGroupid();
                String assignUserFullName = item.getGroupid() == null ? "" : item.getGroupname();

                if (StringUtils.isBlank(assignUserFullName)) {
                    assignUserFullName = StringUtils.extractNameFromEmail(item.getGroupid());
                }

                TicketAssigneeLink ticketAssigneeLink = new TicketAssigneeLink(assignUser, item.getExtraValue(), assignUserFullName);
                assigneeLayout.addComponent(new MCssLayout(ticketAssigneeLink).withWidth("110px"));
                ProgressBarIndicator indicator = new ProgressBarIndicator(totalCountItems, item.getValue().intValue(), false);
                indicator.setWidth("100%");
                assigneeLayout.with(indicator).expand(indicator);
                bodyContent.addComponent(assigneeLayout);
                totalAssignTicketCounts += item.getValue().intValue();
            }
        }
        int totalUnassignTicketsCount = totalCountItems - totalAssignTicketCounts;
        if (totalUnassignTicketsCount > 0) {
            MButton unassignLink = new MButton("No assignee").withStyleName(WebThemes.BUTTON_LINK)
                    .withIcon(UserAvatarControlFactory.createAvatarResource(null, 16)).withListener(clickEvent -> {
                        ProjectTicketSearchCriteria criteria = BeanUtility.deepClone(searchCriteria);
                        criteria.setUnAssignee(new SearchField());
                        EventBusFactory.getInstance().post(new TicketEvent.SearchRequest(UnresolvedTicketsByAssigneeWidget.this,
                                criteria));
                    });
            MHorizontalLayout assigneeLayout = new MHorizontalLayout().withFullWidth();
            assigneeLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            assigneeLayout.addComponent(new MCssLayout(unassignLink).withWidth("110px"));
            ProgressBarIndicator indicator = new ProgressBarIndicator(totalCountItems, totalUnassignTicketsCount, false);
            indicator.setWidth("100%");
            assigneeLayout.with(indicator).expand(indicator);
            bodyContent.addComponent(assigneeLayout);
        }
    }

    private class TicketAssigneeLink extends MButton {
        private static final long serialVersionUID = 1L;

        TicketAssigneeLink(final String assignee, String assigneeAvatarId, final String assigneeFullName) {
            super(StringUtils.trim(assigneeFullName, 25, true));

            this.withListener(clickEvent -> {
                ProjectTicketSearchCriteria criteria = BeanUtility.deepClone(searchCriteria);
                criteria.setAssignUser(StringSearchField.and(assignee));
                criteria.setTypes(CurrentProjectVariables.getRestrictedTicketTypes());
                EventBusFactory.getInstance().post(new TicketEvent.SearchRequest(UnresolvedTicketsByAssigneeWidget.this,
                        criteria));
            }).withWidth("100%").withIcon(UserAvatarControlFactory.createAvatarResource(assigneeAvatarId, 16))
                    .withStyleName(WebThemes.BUTTON_LINK, WebThemes.TEXT_ELLIPSIS);
            UserService service = AppContextUtil.getSpringBean(UserService.class);
            SimpleUser user = service.findUserByUserNameInAccount(assignee, AppUI.getAccountId());
            this.setDescription(CommonTooltipGenerator.generateTooltipUser(UserUIContext.getUserLocale(), user,
                    AppUI.getSiteUrl(), UserUIContext.getUserTimeZone()), ContentMode.HTML);
        }
    }
}
