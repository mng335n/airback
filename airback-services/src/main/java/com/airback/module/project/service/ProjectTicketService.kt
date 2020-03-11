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
import com.airback.core.cache.CacheKey
import com.airback.core.cache.Cacheable
import com.airback.db.arguments.BasicSearchRequest
import com.airback.db.persistence.service.ISearchableService
import com.airback.module.project.domain.ProjectTicket
import com.airback.module.project.domain.criteria.ProjectTicketSearchCriteria
import com.airback.module.user.domain.BillingAccount

/**
 * @author airback Ltd.
 * @since 1.0
 */
interface ProjectTicketService : ISearchableService<ProjectTicketSearchCriteria> {
    fun getAccountsHasOverdueAssignments(searchCriteria: ProjectTicketSearchCriteria): List<BillingAccount>

    fun getProjectsHasOverdueAssignments(searchCriteria: ProjectTicketSearchCriteria): List<Int>

    fun updateAssignmentValue(assignment: ProjectTicket, username: String)

    fun closeSubAssignmentOfMilestone(milestoneId: Int)

    fun findTicket(type: String, typeId: Int): ProjectTicket?

    @Cacheable
    fun findTicketsByCriteria(@CacheKey searchRequest: BasicSearchRequest<ProjectTicketSearchCriteria>): List<*>

    @Cacheable
    fun getTotalTicketsCount(@CacheKey criteria: ProjectTicketSearchCriteria): Int

    @Cacheable
    fun getAssigneeSummary(@CacheKey criteria: ProjectTicketSearchCriteria): List<GroupItem>

    @Cacheable
    fun getPrioritySummary(@CacheKey criteria: ProjectTicketSearchCriteria): List<GroupItem>

    fun updateTicket(ticket: ProjectTicket, username: String)

    fun updateMilestoneId(ticket: ProjectTicket)

    fun removeTicket(ticket: ProjectTicket, username: String)

    fun isTicketIdSatisfyCriteria(type: String, typeId: Int, criteria: ProjectTicketSearchCriteria): Boolean
}
