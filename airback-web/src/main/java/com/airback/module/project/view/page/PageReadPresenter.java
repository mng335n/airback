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
package com.airback.module.project.view.page;

import com.airback.common.i18n.GenericI18Enum;
import com.airback.core.SecureAccessException;
import com.airback.module.page.domain.Page;
import com.airback.module.page.service.PageService;
import com.airback.module.project.CurrentProjectVariables;
import com.airback.module.project.ProjectRolePermissionCollections;
import com.airback.module.project.event.PageEvent;
import com.airback.module.project.view.ProjectBreadcrumb;
import com.airback.module.project.view.ProjectGenericPresenter;
import com.airback.module.project.view.ProjectView;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.EventBusFactory;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.DefaultPreviewFormHandler;
import com.airback.vaadin.mvp.LoadPolicy;
import com.airback.vaadin.mvp.ScreenData;
import com.airback.vaadin.mvp.ViewManager;
import com.airback.vaadin.mvp.ViewScope;
import com.airback.vaadin.web.ui.ConfirmDialogExt;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

/**
 * @author airback Ltd.
 * @since 4.4.0
 */
@LoadPolicy(scope = ViewScope.PROTOTYPE)
public class PageReadPresenter extends ProjectGenericPresenter<PageReadView> {
    private static final long serialVersionUID = 1L;

    public PageReadPresenter() {
        super(PageReadView.class);
    }

    @Override
    protected void postInitView() {
        view.getPreviewFormHandlers().addFormHandler(new DefaultPreviewFormHandler<Page>() {
            @Override
            public void onEdit(Page data) {
                EventBusFactory.getInstance().post(new PageEvent.GotoEdit(this, data));
            }

            @Override
            public void onAdd(Page data) {
                EventBusFactory.getInstance().post(new PageEvent.GotoAdd(this, null));
            }

            @Override
            public void onDelete(final Page data) {
                ConfirmDialogExt.show(UI.getCurrent(),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_TITLE, AppUI.getSiteName()),
                        UserUIContext.getMessage(GenericI18Enum.DIALOG_DELETE_SINGLE_ITEM_MESSAGE),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_YES),
                        UserUIContext.getMessage(GenericI18Enum.ACTION_NO),
                        confirmDialog -> {
                            if (confirmDialog.isConfirmed()) {
                                PageService pageService = AppContextUtil.getSpringBean(PageService.class);
                                pageService.removeResource(data.getPath());
                                EventBusFactory.getInstance().post(new PageEvent.GotoList(this, null));
                            }
                        });
            }
        });
    }

    @Override
    protected void onGo(HasComponents container, ScreenData<?> data) {
        if (CurrentProjectVariables.canRead(ProjectRolePermissionCollections.PAGES)) {
            ProjectView projectView = (ProjectView) container;
            projectView.gotoSubView(ProjectView.PAGE_ENTRY, view);

            Page page = (Page) data.getParams();
            view.previewItem(page);

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoPageRead(page);
        } else {
            throw new SecureAccessException();
        }
    }
}
