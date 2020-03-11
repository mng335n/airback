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
package com.airback.common.service.impl

import com.airback.common.dao.ActivityStreamMapper
import com.airback.common.dao.ActivityStreamMapperExt
import com.airback.common.domain.ActivityStreamWithBLOBs
import com.airback.common.domain.criteria.ActivityStreamSearchCriteria
import com.airback.common.service.ActivityStreamService
import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.ISearchableDAO
import com.airback.db.persistence.service.DefaultService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class ActivityStreamServiceImpl(private val activityStreamMapper: ActivityStreamMapper,
                                private val activityStreamMapperExt: ActivityStreamMapperExt) : DefaultService<Int, ActivityStreamWithBLOBs, ActivityStreamSearchCriteria>(), ActivityStreamService {

    override val crudMapper: ICrudGenericDAO<Int, ActivityStreamWithBLOBs>
        get() = activityStreamMapper as ICrudGenericDAO<Int, ActivityStreamWithBLOBs>

    override val searchMapper: ISearchableDAO<ActivityStreamSearchCriteria>
        get() = activityStreamMapperExt

    override fun save(activityStream: ActivityStreamWithBLOBs): Int? {
        activityStreamMapper.insertAndReturnKey(activityStream)
        return activityStream.id
    }
}