package com.airback.module.project.view.reports;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Img;
import com.airback.core.utils.StringUtils;
import com.airback.html.DivLessFormatter;
import com.airback.module.file.StorageUtils;
import com.airback.module.project.ProjectLinkGenerator;
import com.airback.module.project.i18n.StandupI18nEnum;
import com.airback.module.project.service.StandupReportService;
import com.airback.module.user.domain.SimpleUser;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.AppUI;
import com.airback.vaadin.TooltipHelper;
import com.airback.vaadin.UserUIContext;
import com.airback.vaadin.ui.ELabel;
import com.airback.vaadin.web.ui.WebThemes;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.time.LocalDate;
import java.util.List;

/**
 * @author airback Ltd.
 * @since 4.0
 */
class StandupMissingComp extends MVerticalLayout {
    private static final long serialVersionUID = 5332956503787026253L;

    StandupMissingComp(Integer projectId, LocalDate date) {
        this.withSpacing(false).withMargin(false);

        StandupReportService standupReportService = AppContextUtil.getSpringBean(StandupReportService.class);
        List<SimpleUser> someGuys = standupReportService.findUsersNotDoReportYet(projectId, date, AppUI.getAccountId());
        if (someGuys.size() == 0) {
            //this.addComponent(new Label(UserUIContext.getMessage(GenericI18Enum.EXT_NO_ITEM)));
        } else {
            with(new ELabel(UserUIContext.getMessage(StandupI18nEnum.STANDUP_MEMBER_NOT_REPORT)).withStyleName(ValoTheme.LABEL_H3));
            someGuys.forEach(user -> this.with(ELabel.html(buildMemberLink(projectId, user))));
        }
    }

    private String buildMemberLink(Integer projectId, SimpleUser user) {
        DivLessFormatter div = new DivLessFormatter();
        Img userAvatar = new Img("", StorageUtils.getAvatarPath(user.getAvatarid(), 16)).setCSSClass(WebThemes.CIRCLE_BOX);
        A userLink = new A().setId("tag" + TooltipHelper.TOOLTIP_ID).
                setHref(ProjectLinkGenerator.generateProjectMemberLink(projectId, user.getUsername()));

        userLink.setAttribute("onmouseover", TooltipHelper.userHoverJsFunction(user.getUsername()));
        userLink.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction());
        userLink.appendText(StringUtils.trim(user.getDisplayName(), 30, true));

        div.appendChild(userAvatar, DivLessFormatter.EMPTY_SPACE, userLink);
        return div.write();
    }
}
