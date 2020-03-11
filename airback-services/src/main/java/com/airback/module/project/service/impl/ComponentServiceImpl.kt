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

import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.aspect.Traceable
import com.airback.common.ModuleNameConstants
import com.airback.core.airbackException
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.ComponentMapper
import com.airback.module.project.dao.ComponentMapperExt
import com.airback.module.project.domain.Component
import com.airback.module.project.domain.ComponentExample
import com.airback.module.project.domain.SimpleComponent
import com.airback.module.project.domain.criteria.ComponentSearchCriteria
import com.airback.module.project.service.ComponentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author airback Ltd.
 * @since 1.0.0
 */
@Service
@Transactional
@Traceable(nameField = "name", extraFieldName = "projectid")
class ComponentServiceImpl(private val componentMapper: ComponentMapper,
                           private val componentMapperExt: ComponentMapperExt) : DefaultService<Int, Component, ComponentSearchCriteria>(), ComponentService {

    override val crudMapper: ICrudGenericDAO<Int, Component>
        get() = componentMapper as ICrudGenericDAO<Int, Component>

    override val searchMapper: ISearchableDAO<ComponentSearchCriteria>
        get() = componentMapperExt

    override fun findById(componentId: Int, sAccountId: Int): SimpleComponent? =
            componentMapperExt.findComponentById(componentId)

    override fun saveWithSession(record: Component, username: String?): Int {
        // check whether there is exiting record
        val ex = ComponentExample()
        ex.createCriteria().andNameEqualTo(record.name).andProjectidEqualTo(record.projectid)

        val count = componentMapper.countByExample(ex)
        return if (count > 0) {
            throw airbackException("There is an existing record has name ${record.name}")
        } else {
            super.saveWithSession(record, username)
        }
    }

    companion object {
        init {
            ClassInfoMap.put(ComponentServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.COMPONENT))
        }
    }
}