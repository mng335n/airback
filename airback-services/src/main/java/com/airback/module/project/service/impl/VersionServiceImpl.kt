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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.airback.module.project.service.impl

import com.airback.aspect.ClassInfo
import com.airback.aspect.ClassInfoMap
import com.airback.aspect.Traceable
import com.airback.common.ModuleNameConstants
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.dao.VersionMapper
import com.airback.module.project.dao.VersionMapperExt
import com.airback.module.project.domain.SimpleVersion
import com.airback.module.project.domain.Version
import com.airback.module.project.domain.criteria.VersionSearchCriteria
import com.airback.module.project.service.VersionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author airback Ltd.
 * @since 1.0.0
 */
@Service
@Transactional
@Traceable(nameField = "name", extraFieldName = "projectid")
class VersionServiceImpl(private val versionMapper: VersionMapper,
                         private val versionMapperExt: VersionMapperExt) : DefaultService<Int, Version, VersionSearchCriteria>(), VersionService {

    override val crudMapper: ICrudGenericDAO<Int, Version>
        get() = versionMapper as ICrudGenericDAO<Int, Version>

    override val searchMapper: ISearchableDAO<VersionSearchCriteria>
        get() = versionMapperExt

    override fun findById(versionId: Int, sAccountId: Int): SimpleVersion? =
            versionMapperExt.findVersionById(versionId)

    companion object {
        init {
            ClassInfoMap.put(VersionServiceImpl::class.java, ClassInfo(ModuleNameConstants.PRJ, ProjectTypeConstants.VERSION))
        }
    }
}
