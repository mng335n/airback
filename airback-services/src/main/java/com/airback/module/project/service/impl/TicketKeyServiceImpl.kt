package com.airback.module.project.service.impl

import com.airback.module.project.dao.TicketKeyMapper
import com.airback.module.project.dao.TicketKeyMapperExt
import com.airback.module.project.domain.TicketKey
import com.airback.module.project.service.TicketKeyService
import org.springframework.stereotype.Service

/**
 * @author airback Ltd
 * @since 7.0.2
 */
@Service
class TicketKeyServiceImpl(private val ticketKeyMapper: TicketKeyMapper,
                           private val ticketKeyMapperExt: TicketKeyMapperExt) : TicketKeyService {
    override fun getMaxKey(projectId: Int): Int? = ticketKeyMapperExt.getMaxKey(projectId)

    override fun saveKey(projectId: Int, ticketId: Int, ticketType: String, ticketKey: Int) {
        val key = TicketKey()
        key.projectid = projectId
        key.ticketid = ticketId
        key.tickettype = ticketType
        key.ticketkey = ticketKey
        ticketKeyMapper.insertAndReturnKey(key)
    }

    override fun getNextKey(projectId: Int, currentKey: Int): Int? = ticketKeyMapperExt.getNextKey(projectId, currentKey)

    override fun getPreviousKey(projectId: Int, currentKey: Int): Int? = ticketKeyMapperExt.getPreviousKey(projectId, currentKey)

    override fun getTicketKeyByPrjShortNameAndKey(sAccountId: Int, prjShortName: String, ticketKey: Int): TicketKey =
            ticketKeyMapperExt.getTicketKeyByPrjShortNameAndKey(sAccountId, prjShortName, ticketKey)
}