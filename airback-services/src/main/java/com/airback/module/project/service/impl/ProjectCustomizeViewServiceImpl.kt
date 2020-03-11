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
package com.airback.module.project.service.impl

import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import com.airback.module.project.dao.ProjectCustomizeViewMapper
import com.airback.module.project.domain.ProjectCustomizeView
import com.airback.module.project.service.ProjectCustomizeViewService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 4.4.0
 */
@Service
class ProjectCustomizeViewServiceImpl(private val projectCustomizeMapper: ProjectCustomizeViewMapper) : DefaultCrudService<Int, ProjectCustomizeView>(), ProjectCustomizeViewService {

    override val crudMapper: ICrudGenericDAO<Int, ProjectCustomizeView>
        get() = projectCustomizeMapper as ICrudGenericDAO<Int, ProjectCustomizeView>
}
