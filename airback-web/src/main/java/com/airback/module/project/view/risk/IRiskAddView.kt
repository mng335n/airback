package com.airback.module.project.view.risk

import com.airback.module.project.domain.SimpleRisk
import com.airback.vaadin.mvp.IFormAddView
import com.airback.vaadin.web.ui.field.AttachmentUploadField

/**
 * @author airback Ltd
 * @since 7.0
 */
interface IRiskAddView : IFormAddView<SimpleRisk> {

    fun getAttachUploadField(): AttachmentUploadField
}