package com.airback.common

import com.airback.core.AbstractNotification

/**
 * @author airback Ltd
 * @since 6.0.0
 */
class EntryUpdateNotification(val targetUser:String, val module: String, val type:String, val typeId:String, val message: String) : AbstractNotification(AbstractNotification.NEWS)
