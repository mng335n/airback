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
package com.airback.module.project.service

import com.airback.common.domain.GroupItem
import com.airback.core.cache.CacheEvict
import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.project.domain.BugWithBLOBs
import com.airback.module.project.domain.SimpleBug
import com.airback.module.project.domain.criteria.BugSearchCriteria

/**
 * @author airback Ltd.
 * @since 1.0
 */
interface BugService : IDefaultService<Int, BugWithBLOBs, BugSearchCriteria> {

    @Cacheable
    fun findById(bugId: Int, @CacheKey sAccountId: Int): SimpleBug?

    @Cacheable
    fun getStatusSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getPrioritySummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getAssignedDefectsSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getResolutionDefectsSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getReporterDefectsSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getVersionDefectsSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>

    @Cacheable
    fun getComponentDefectsSummary(@CacheKey criteria: BugSearchCriteria): List<GroupItem>
}
