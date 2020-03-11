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
package com.airback.module.project.view.file;

import com.airback.common.i18n.FileI18nEnum;
import com.airback.core.airbackException;
import com.airback.core.utils.StringUtils;
import com.airback.module.ecm.domain.Folder;
import com.airback.module.project.domain.criteria.FileSearchCriteria;
import com.airback.module.project.i18n.ProjectCommonI18nEnum;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.event.HasSearchHandlers;
import com.airback.vaadin.event.SearchHandler;
import com.airback.vaadin.mvp.CacheableComponent;
import com.airback.vaadin.mvp.ViewComponent;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.CommonUIFactory;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
@ViewComponent
public class FileBreadcrumb extends MHorizontalLayout implements CacheableComponent, HasSearchHandlers<FileSearchCriteria> {
    private static final long serialVersionUID = 1L;

    private List<SearchHandler<FileSearchCriteria>> handlers;

    private String rootFolderPath;

    public FileBreadcrumb() {
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
        initBreadcrumb();
    }

    void initBreadcrumb() {
        removeAllComponents();

        MButton documentBtnLink = generateBreadcrumbLink(UserUIContext.getMessage(ProjectCommonI18nEnum.VIEW_FILE), clickEvent -> {
            FileSearchCriteria criteria = new FileSearchCriteria();
            criteria.setBaseFolder(rootFolderPath);
            criteria.setRootFolder(rootFolderPath);
            notifySearchHandler(criteria);
        });
        this.with(documentBtnLink);
    }

    void gotoFolder(Folder folder) {
        initBreadcrumb();

        if (folder == null) {
            throw new airbackException("Folder is null");
        } else {
            String folderPath = folder.getPath();
            if (!folderPath.startsWith(rootFolderPath)) {
                throw new airbackException("Invalid path " + rootFolderPath + "---" + folderPath);
            }

            String remainPath = folderPath.substring(rootFolderPath.length());
            if (remainPath.startsWith("/")) {
                remainPath = remainPath.substring(1);
            }

            MButton btn1 = null, btn2 = null;
            int index;
            if ((index = remainPath.lastIndexOf('/')) != -1) {
                String pathName = remainPath.substring(index + 1);
                String newPath = remainPath.substring(0, index);
                remainPath = newPath;
                btn2 = new MButton(StringUtils.trim(pathName, 25, true), clickEvent -> {
                    FileSearchCriteria criteria = new FileSearchCriteria();
                    criteria.setBaseFolder(rootFolderPath + "/" + newPath);
                    criteria.setRootFolder(rootFolderPath);
                    notifySearchHandler(criteria);
                }).withDescription(pathName).withStyleName(WebThemes.BUTTON_LINK);
            }

            if ((index = remainPath.lastIndexOf('/')) != -1) {
                String pathName = remainPath.substring(index + 1);
                String newPath = remainPath.substring(0, index);
                btn1 = new MButton(StringUtils.trim(pathName, 25, true), clickEvent -> {
                    FileSearchCriteria criteria = new FileSearchCriteria();
                    criteria.setBaseFolder(rootFolderPath + "/" + newPath);
                    criteria.setRootFolder(rootFolderPath);
                    notifySearchHandler(criteria);
                }).withDescription(pathName).withStyleName(WebThemes.BUTTON_LINK);
            } else {
                String newPath = remainPath;
                if (StringUtils.isNotBlank(newPath)) {
                    btn1 = new MButton(StringUtils.trim(newPath, 25, true), clickEvent -> {
                        FileSearchCriteria criteria = new FileSearchCriteria();
                        criteria.setBaseFolder(rootFolderPath + "/" + newPath);
                        criteria.setRootFolder(rootFolderPath);
                        notifySearchHandler(criteria);
                    }).withDescription(newPath).withStyleName(WebThemes.BUTTON_LINK);
                }
            }

            if (btn1 != null) {
                with(ELabel.html(VaadinIcons.ANGLE_RIGHT.getHtml()), btn1);
            }

            if (btn2 != null) {
                with(ELabel.html(VaadinIcons.ANGLE_RIGHT.getHtml()), btn2);
            }
        }
    }

    private static MButton generateBreadcrumbLink(String linkName, Button.ClickListener listener) {
        return CommonUIFactory.createButtonTooltip(StringUtils.trim(linkName, 25, true),
                linkName, listener).withStyleName(WebThemes.BUTTON_LINK);
    }

    @Override
    public void addSearchHandler(SearchHandler<FileSearchCriteria> handler) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(handler);
    }

    @Override
    public void notifySearchHandler(FileSearchCriteria criteria) {
        if (handlers != null) {
            handlers.forEach(handler -> handler.onSearch(criteria));
        }
    }

    @Override
    public void setTotalCountNumber(Integer totalCountNumber) {

    }
}
