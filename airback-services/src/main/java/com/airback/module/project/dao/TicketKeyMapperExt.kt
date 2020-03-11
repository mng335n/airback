package com.airback.module.project.dao

import com.airback.module.project.domain.TicketKey

/**
 * @author airback
 * @since 7.0.2
 */
interface TicketKeyMapperExt {
    fun getMaxKey(projectId: Int): Int?

    fun getNextKey(projectId: Int, currentKey:Int): Int?

    fun getPreviousKey(projectId: Int, currentKey: Int): Int?

    fun getTicketKeyByPrjShortNameAndKey(sAccountId: Int, prjShortName:String, ticketKey:Int): TicketKey
}