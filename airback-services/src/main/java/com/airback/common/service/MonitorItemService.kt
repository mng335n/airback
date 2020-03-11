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
package com.airback.common.service

import com.airback.cache.IgnoreCacheClass
import com.airback.common.domain.MonitorItem
import com.airback.common.domain.criteria.MonitorSearchCriteria
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.user.domain.SimpleUser

/**
 * @author airback Ltd.
 * @since 1.0
 */
@IgnoreCacheClass
interface MonitorItemService : IDefaultService<Int, MonitorItem, MonitorSearchCriteria> {

    fun isUserWatchingItem(username: String, type: String, typeId: String): Boolean

    fun getWatchers(type: String, typeId: Int): List<SimpleUser>

    fun saveMonitorItems(monitorItems: Collection<MonitorItem>)
}
