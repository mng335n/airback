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

import com.airback.common.i18n.GenericI18Enum;
import com.airback.reporting.ReportExportType;
import com.airback.reporting.ReportTemplateExecutor;
import com.airback.reporting.RpFieldsBuilder;
import com.airback.reporting.SimpleReportTemplateExecutor;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.MassItemActionHandler;
import com.airback.vaadin.event.ViewItemAction;
import com.airback.vaadin.reporting.ReportStreamSource;
import com.airback.vaadin.web.ui.table.IPagedTable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import java.util.HashMap;
import java.util.Map;

/**
 * @author airback Ltd.
 * @since 2.0
 */
public abstract class DefaultMassEditActionHandler implements MassItemActionHandler {
    private ListSelectionPresenter presenter;

    public DefaultMassEditActionHandler(ListSelectionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onSelect(String id) {
        if (ViewItemAction.DELETE_ACTION.equals(id)) {
            ConfirmDialogExt.show(UI.getCurrent(), UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                    UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_MULTIPLE_ITEMS_MESSAGE),
                    UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                    UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                    confirmDialog -> {
                        if (confirmDialog.isConfirmed()) {
                            presenter.deleteSelectedItems();
                        }
                    });
        } else {
            onSelectExtra(id);
        }

    }

    @Override
    public StreamResource buildStreamResource(ReportExportType exportType) {
        IPagedTable pagedBeanTable = ((IListView) presenter.getView()).getPagedBeanGrid();
        final Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("siteUrl", AppUI.getSiteUrl());
        additionalParameters.put(SimpleReportTemplateExecutor.CRITERIA, presenter.searchCriteria);
        ReportTemplateExecutor reportTemplateExecutor;
        if (presenter.isSelectAll) {
            reportTemplateExecutor = new SimpleReportTemplateExecutor.AllItems(UserUIContext.getUserTimeZone(),
                    UserUIContext.getUserLocale(), getReportTitle(),
                    new RpFieldsBuilder(pagedBeanTable.getDisplayColumns()), exportType, getReportModelClassType(),
                    presenter.getSearchService());
        } else {
            reportTemplateExecutor = new SimpleReportTemplateExecutor.ListData(UserUIContext.getUserTimeZone(),
                    UserUIContext.getUserLocale(), getReportTitle(),
                    new RpFieldsBuilder(pagedBeanTable.getDisplayColumns()), exportType, presenter.getSelectedItems(),
                    getReportModelClassType());
        }
        return new StreamResource(new ReportStreamSource(reportTemplateExecutor) {
            @Override
            protected void initReportParameters(Map<String, Object> parameters) {
                parameters.putAll(additionalParameters);
            }
        }, exportType.getDefaultFileName());
    }

    protected void onSelectExtra(String id) {}

    protected abstract Class<?> getReportModelClassType();

    protected abstract String getReportTitle();
}
