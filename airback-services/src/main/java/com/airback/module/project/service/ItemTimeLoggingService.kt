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

import com.airback.core.cache.CacheEvict
import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.persistence.service.IDefaultService
import com.airback.module.project.domain.ItemTimeLogging
import com.airback.module.project.domain.criteria.ItemTimeLoggingSearchCriteria

/**
 * @author airback Ltd.
 * @since 1.0
 */
interface ItemTimeLoggingService : IDefaultService<Int, ItemTimeLogging, ItemTimeLoggingSearchCriteria> {

    @Cacheable
    fun getTotalHoursByCriteria(@CacheKey criteria: ItemTimeLoggingSearchCriteria): Double

    @CacheEvict
    fun batchSaveTimeLogging(timeLoggings: List<ItemTimeLogging>, @CacheKey sAccountId: Int)

    @Cacheable
    fun getTotalBillableHoursByMilestone(milestoneId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getTotalNonBillableHoursByMilestone(milestoneId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getRemainHoursByMilestone(milestoneId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getTotalBillableHoursByComponent(componentId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getTotalNonBillableHoursByComponent(componentId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getRemainHoursByComponent(componentId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getTotalBillableHoursByVersion(versionId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getTotalNonBillableHoursByVersion(versionId: Int, @CacheKey sAccountId: Int): Double

    @Cacheable
    fun getRemainHoursByVersion(versionId: Int, @CacheKey sAccountId: Int): Double
}
