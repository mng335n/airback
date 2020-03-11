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
package com.airback.module.project.view.task;

import com.airback.module.project.domain.Component;
import com.airback.module.project.domain.SimpleTask;
import com.airback.module.project.domain.Version;
import com.airback.vaadin.event.HasEditFormHandlers;
import com.airback.vaadin.mvp.IFormAddView;
import com.airback.vaadin.web.ui.field.AttachmentUploadField;

import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public interface TaskAddView extends IFormAddView<SimpleTask> {

    HasEditFormHandlers<SimpleTask> getEditFormHandlers();

    AttachmentUploadField getAttachUploadField();

    List<String> getFollowers();

    List<Component> getComponents();

    List<Version> getAffectedVersions();
}
