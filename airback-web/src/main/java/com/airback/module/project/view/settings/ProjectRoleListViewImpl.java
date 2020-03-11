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
package com.airback.module.project.view.settings;

import com.airback.common.TableViewField;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectLinkGenerator;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.domain.ProjectRole;
import com.airback.module.project.domain.SimpleProjectRole;
import com.airback.module.project.domain.criteria.ProjectRoleSearchCriteria;
import com.airback.module.project.service.ProjectRoleService;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasMassItemActionHandler;
import com.airback.vaadin.event.HasSearchHandlers;
import com.airback.vaadin.event.HasSelectableItemHandlers;
import com.airback.vaadin.event.HasSelectionOptionHandlers;
import com.airback.vaadin.mvp.AbstractVerticalPageView;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.DefaultMassItemActionHandlerContainer;
import com.airback.vaadin.web.ui.*;
import com.airback.vaadin.web.ui.table.AbstractPagedBeanTable;
import com.airback.vaadin.web.ui.table.DefaultPagedBeanTable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.viritin.layouts.MCssLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.Arrays;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class ProjectRoleListViewImpl extends AbstractVerticalPageView implements ProjectRoleListView {
    private static final long serialVersionUID = 1L;

    private ProjectRoleSearchPanel searchPanel;
    private SelectionOptionButton selectOptionButton;
    private DefaultPagedBeanTable<ProjectRoleService, ProjectRoleSearchCriteria, SimpleProjectRole> tableItem;
    private MVerticalLayout listLayout;
    private DefaultMassItemActionHandlerContainer tableActionControls;
    private Label selectedItemsNumberLabel = new Label();

    public ProjectRoleListViewImpl() {
        this.setMargin(new MarginInfo(false, true, true, true));
        searchPanel = new ProjectRoleSearchPanel();

        listLayout = new MVerticalLayout().withMargin(false).withSpacing(false);
        with(searchPanel, listLayout).expand(listLayout);

        this.generateDisplayTable();
    }

    private void generateDisplayTable() {
        tableItem = new DefaultPagedBeanTable<>(AppContextUtil.getSpringBean(ProjectRoleService.class),
                SimpleProjectRole.class, new TableViewField(null, "selected", WebUIConstants.TABLE_CONTROL_WIDTH),
                Arrays.asList(new TableViewField(GenericI18Enum.FORM_NAME, "rolename", WebUIConstants.TABLE_EX_LABEL_WIDTH),
                        new TableViewField(GenericI18Enum.FORM_DESCRIPTION, "description", WebUIConstants.TABLE_EX_LABEL_WIDTH)));

        tableItem.addGeneratedColumn("selected", (source, itemId, columnId) -> {
            final SimpleProjectRole role = tableItem.getBeanByIndex(itemId);
            CheckBoxDecor cb = new CheckBoxDecor("", role.isSelected());
            cb.addValueChangeListener(valueChangeEvent -> tableItem.fireSelectItemEvent(role));
            role.setExtraData(cb);
            return cb;
        });

        tableItem.addGeneratedColumn("rolename", (source, itemId, columnId) -> {
            ProjectRole role = tableItem.getBeanByIndex(itemId);
            return new LabelLink(role.getRolename(),
                    ProjectLinkGenerator.generateRolePreviewLink(role.getProjectid(), role.getId()));
        });

        tableItem.setWidth("100%");
        listLayout.with(constructTableActionControls(), tableItem).expand(tableItem);
    }

    @Override
    public void showNoItemView() {

    }

    @Override
    public HasSearchHandlers<ProjectRoleSearchCriteria> getSearchHandlers() {
        return this.searchPanel;
    }

    private ComponentContainer constructTableActionControls() {
        MCssLayout layout = new MCssLayout().withStyleName(WebThemes.TABLE_ACTION_CONTROLS).withFullWidth();

        selectOptionButton = new SelectionOptionButton(tableItem);

        tableActionControls = new DefaultMassItemActionHandlerContainer();
        if (CurrentProjectVariables.canAccess(ProjectRolePermissionCollections.ROLES)) {
            tableActionControls.addDeleteActionItem();
        }

        tableActionControls.addDownloadPdfActionItem();
        tableActionControls.addDownloadExcelActionItem();
        tableActionControls.addDownloadCsvActionItem();

        layout.add(selectOptionButton, tableActionControls, this.selectedItemsNumberLabel);
        return layout;
    }

    @Override
    public void enableActionControls(int numOfSelectedItems) {
        tableActionControls.setVisible(true);
        selectedItemsNumberLabel.setValue(UserUIContext.getMessage(GenericI18Enum.TABLE_SELECTED_ITEM_TITLE, numOfSelectedItems));
    }

    @Override
    public void disableActionControls() {
        tableActionControls.setVisible(false);
        selectOptionButton.setSelectedCheckbox(false);
        selectedItemsNumberLabel.setValue("");
    }

    @Override
    public HasSelectionOptionHandlers getOptionSelectionHandlers() {
        return this.selectOptionButton;
    }

    @Override
    public HasMassItemActionHandler getPopupActionHandlers() {
        return this.tableActionControls;
    }

    @Override
    public HasSelectableItemHandlers<SimpleProjectRole> getSelectableItemHandlers() {
        return this.tableItem;
    }

    @Override
    public AbstractPagedBeanTable<ProjectRoleSearchCriteria, SimpleProjectRole> getPagedBeanGrid() {
        return this.tableItem;
    }
}
