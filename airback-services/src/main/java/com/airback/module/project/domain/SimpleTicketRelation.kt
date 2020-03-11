package com.airback.module.project.domain

/**
 *
 * @author airback Ltd
 * @since 7.0.2
 */
class SimpleTicketRelation : TicketRelation() {
    var ticketKey: Int? = null
    var ticketName: String? = null
    var ticketStatus: String? = null
    var typeKey: Int? = null
    var typeName: String? = null
    var typeStatus: String? = null
    var ltr: Boolean = true
}