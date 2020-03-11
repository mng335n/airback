package com.airback.form.service.impl

import com.airback.form.service.MasterFormService
import com.airback.form.view.builder.type.DynaForm
import org.springframework.stereotype.Service

/**
 * @author airback Ltd
 * @since 6.0.0
 */
@Service
class MasterFormServiceImpl : MasterFormService {

    override fun findCustomForm(sAccountId: Int, moduleName: String): DynaForm? = null

    override fun saveCustomForm(sAccountId: Int, moduleName: String, form: DynaForm) {
    }
}