package com.airback.module.project.view.reports;

import com.airback.vaadin.mvp.PageView;

import java.time.LocalDate;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 1.0
 */
public interface StandupListView extends PageView {
    /**
     * @param projectKeys
     */
    void display(List<Integer> projectKeys, LocalDate date);
}
