package com.airback.vaadin.ui;

import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

/**
 * @author airback Ltd
 * @since 7.0.0
 */
public class FormSection extends Panel {
    public FormSection(String caption) {
        this(caption, null);
    }

    public FormSection(String caption, Component content) {
        super(caption, content);
        this.addStyleName(WebThemes.FORM_SECTION);
        UIUtils.makeStackPanel(this);
    }
}
