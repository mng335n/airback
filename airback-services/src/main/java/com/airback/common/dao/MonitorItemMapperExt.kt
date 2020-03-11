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
package com.airback.common.dao

import com.airback.common.domain.MonitorItem
import com.airback.common.domain.criteria.MonitorSearchCriteria
import com.airback.db.persistence.ISearchableDAO
import com.airback.module.user.domain.SimpleUser
import org.apache.ibatis.annotations.Param

/**
 * @author airback Ltd
 * @since 1.0.0
 */
interface MonitorItemMapperExt : ISearchableDAO<MonitorSearchCriteria> {

    fun saveMonitorItems(@Param("monitors") monitorItems: Collection<MonitorItem>)

    fun getWatchers(@Param("type") type: String, @Param("typeId") typeId: Int): List<SimpleUser>
}
