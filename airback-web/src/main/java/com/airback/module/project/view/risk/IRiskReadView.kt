package com.airback.module.project.view.risk

import com.airback.module.project.domain.SimpleRisk
import com.airback.vaadin.event.HasPreviewFormHandlers
import com.airback.vaadin.mvp.IPreviewView

interface IRiskReadView : IPreviewView<SimpleRisk> {
    fun getPreviewFormHandlers(): HasPreviewFormHandlers<SimpleRisk>
}