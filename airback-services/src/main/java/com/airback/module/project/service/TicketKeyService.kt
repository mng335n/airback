package com.airback.module.project.service

import com.airback.module.project.domain.TicketKey

/**
 * @author airback Ltd
 * @since 7.0.2
 */
interface TicketKeyService {

    fun getTicketKeyByPrjShortNameAndKey(sAccountId: Int, prjShortName:String, key:Int): TicketKey?

    fun getMaxKey(projectId: Int): Int?

    fun getNextKey(projectId: Int, currentKey:Int): Int?

    fun getPreviousKey(projectId: Int, currentKey: Int): Int?

    fun saveKey(projectId: Int, ticketId:Int, ticketType:String, ticketKey: Int)
}