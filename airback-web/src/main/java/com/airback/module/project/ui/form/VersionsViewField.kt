package com.airback.module.project.ui.form

import com.hp.gagawa.java.elements.A
import com.airback.common.i18n.OptionI18nEnum
import com.airback.core.utils.StringUtils
import com.airback.module.project.ProjectLinkGenerator
import com.airback.module.project.domain.Version
import com.airback.vaadin.ui.ELabel
import com.airback.vaadin.web.ui.WebThemes
import com.vaadin.ui.Component
import com.vaadin.ui.CustomField
import org.apache.commons.collections4.CollectionUtils
import org.vaadin.viritin.layouts.MCssLayout

/**
 * @author airback Ltd
 * @since 7.0.0
 */
class VersionsViewField : CustomField<Collection<Version>>() {

    private val containerLayout: MCssLayout = MCssLayout()

    override fun initContent(): Component = containerLayout

    override fun doSetValue(versions: Collection<Version>) {
        if (CollectionUtils.isNotEmpty(versions)) {
            versions.forEach { containerLayout.addComponent(buildVersionLink(it)) }
        }
    }

    private fun buildVersionLink(version: Version): ELabel {
        val componentLink = A(ProjectLinkGenerator.generateVersionPreviewLink(version.projectid!!, version.id!!))
                .appendText(StringUtils.trim(version.name, 25, true))

        val lbl = ELabel.html(componentLink.write()).withStyleName(WebThemes.FIELD_NOTE)

        if (version.status != null && version.status == OptionI18nEnum.StatusI18nEnum.Closed.name) {
            lbl.addStyleName(WebThemes.LINK_COMPLETED)
        }

        return lbl
    }

    override fun getValue(): Collection<Version>? = null
}
