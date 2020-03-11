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

import com.airback.db.persistence.ICrudGenericDAO
import com.airback.db.persistence.service.DefaultCrudService
import com.airback.module.project.ProjectTypeConstants
import com.airback.module.project.TicketRelationConstants
import com.airback.module.project.dao.TicketRelationMapper
import com.airback.module.project.dao.TicketRelationMapperExt
import com.airback.module.project.domain.*
import com.airback.module.project.service.TicketRelationService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd.
 * @since 1.0
 */
@Service
class TicketRelationServiceImpl(private val ticketRelationMapper: TicketRelationMapper,
                                private val ticketRelationMapperExt: TicketRelationMapperExt) : DefaultCrudService<Int, TicketRelation>(), TicketRelationService {

    override val crudMapper: ICrudGenericDAO<Int, TicketRelation>
        get() = ticketRelationMapper as ICrudGenericDAO<Int, TicketRelation>

    override fun saveAffectedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        insertAffectedVersionsOfTicket(ticketId, ticketType, versions)
    }

    private fun insertAffectedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        versions?.forEach {
            val relatedItem = TicketRelation()
            relatedItem.ticketid = ticketId
            relatedItem.tickettype = ticketType
            relatedItem.typeid = it.id
            relatedItem.type = ProjectTypeConstants.VERSION
            relatedItem.rel = TicketRelationConstants.AFF_VERSION
            ticketRelationMapper.insert(relatedItem)
        }
    }

    override fun saveFixedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        insertFixedVersionsOfTicket(ticketId, ticketType, versions)
    }

    private fun insertFixedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        versions?.forEach {
            val relatedItem = TicketRelation()
            relatedItem.ticketid = ticketId
            relatedItem.tickettype = ticketType
            relatedItem.typeid = it.id
            relatedItem.type = ProjectTypeConstants.VERSION
            relatedItem.rel = TicketRelationConstants.FIX_VERSION
            ticketRelationMapper.insert(relatedItem)
        }
    }

    override fun saveComponentsOfTicket(ticketId: Int, ticketType: String, components: List<Component>?) {
        insertComponentsOfTicket(ticketId, ticketType, components)
    }

    private fun insertComponentsOfTicket(ticketId: Int, ticketType: String, components: List<Component>?) {
        components?.forEach {
            val relatedItem = TicketRelation()
            relatedItem.ticketid = ticketId
            relatedItem.tickettype = ticketType
            relatedItem.typeid = it.id
            relatedItem.type = ProjectTypeConstants.COMPONENT
            relatedItem.rel = TicketRelationConstants.COMPONENT
            ticketRelationMapper.insert(relatedItem)
        }
    }

    private fun deleteTrackerBugRelatedItem(ticketId: Int, ticketType: String, type: String) {
        val ex = TicketRelationExample()
        ex.createCriteria().andTicketidEqualTo(ticketId).andTickettypeEqualTo(ticketType).andTypeEqualTo(type)
        ticketRelationMapper.deleteByExample(ex)
    }


    override fun updateAffectedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        deleteTrackerBugRelatedItem(ticketId, ticketType, TicketRelationConstants.AFF_VERSION)
        if (versions != null) {
            insertAffectedVersionsOfTicket(ticketId, ticketType, versions)
        }
    }

    override fun updateFixedVersionsOfTicket(ticketId: Int, ticketType: String, versions: List<Version>?) {
        deleteTrackerBugRelatedItem(ticketId, ticketType, TicketRelationConstants.FIX_VERSION)
        if (versions != null) {
            insertFixedVersionsOfTicket(ticketId, ticketType, versions)
        }
    }

    override fun updateComponentsOfTicket(ticketId: Int, ticketType: String, components: List<Component>?) {
        deleteTrackerBugRelatedItem(ticketId, ticketType, TicketRelationConstants.COMPONENT)
        if (components != null) {
            insertComponentsOfTicket(ticketId, ticketType, components)
        }
    }

    override fun findRelatedTickets(ticketId: Int, ticketType: String): List<SimpleTicketRelation> = ticketRelationMapperExt.findRelatedTickets(ticketId, ticketType)

    override fun removeRelationsByRel(ticketId: Int, ticketType: String, rel: String) {
        val ex = TicketRelationExample()
        ex.createCriteria().andTicketidEqualTo(ticketId).andTickettypeEqualTo(ticketType).andRelEqualTo(rel)
        ticketRelationMapper.deleteByExample(ex)
    }
}
