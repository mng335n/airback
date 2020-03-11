package com.airback.module.project.dao

import com.airback.module.project.domain.SimpleTicketRelation
import org.apache.ibatis.annotations.Param

interface TicketRelationMapperExt {
    fun findRelatedTickets(@Param("ticketId") ticketId:Int, @Param("ticketType")ticketType:String): List<SimpleTicketRelation>
}