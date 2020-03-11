/**
 * Copyright © airback
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.airback.module.project.view.page;

import com.google.common.collect.Ordering;
import com.airback.common.domain.SimpleComment;
import com.airback.common.domain.criteria.CommentSearchCriteria;
import com.airback.common.i18n.GenericI18Enum;
import com.airback.common.service.CommentService;
import com.airback.core.utils.StringUtils;
import com.airback.db.arguments.BasicSearchRequest;
import com.airback.db.arguments.StringSearchField;
import com.airback.module.page.domain.Page;
import com.airback.module.project.ProjectTypeConstants;
import com.airback.reporting.ReportExportType;
import com.airback.reporting.ReportTemplateExecutor;
import com.airback.spring.AppContextUtil;
import com.airback.vaadin.UserUIContext;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.MultiPageListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;

/**
 * @author airback Ltd
 * @since 5.4.0
 */
class PageReportTemplateExecutor extends ReportTemplateExecutor {

    private static Ordering dateComparator = new Ordering() {
        @Override
        public int compare(Object o1, Object o2) {
            try {
                Date createTime1 = (Date) PropertyUtils.getProperty(o1, "createdtime");
                Date createTime2 = (Date) PropertyUtils.getProperty(o2, "createdtime");
                return createTime1.compareTo(createTime2);
            } catch (Exception e) {
                return 0;
            }
        }
    };
    private JasperReportBuilder reportBuilder;
    private MultiPageListBuilder titleContent;

    PageReportTemplateExecutor(String reportTitle) {
        super(UserUIContext.getUserTimeZone(), UserUIContext.getUserLocale(), reportTitle, ReportExportType.PDF);
    }

    @Override
    public void initReport() throws Exception {
        reportBuilder = report();
        titleContent = cmp.multiPageList();
        titleContent.add(defaultTitleComponent());
        reportBuilder.setParameters(getParameters());
        reportBuilder.title(titleContent).setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
                .pageFooter(cmp.pageXofY().setStyle(getReportStyles().getBoldCenteredStyle()))
                .setLocale(getLocale());
    }

    @Override
    public void fillReport() throws DRException {
        Map<String, Object> parameters = this.getParameters();
        Page bean = (Page) parameters.get("bean");
        printForm(bean);
        printActivities(bean);
    }

    @Override
    public void outputReport(OutputStream outputStream) throws IOException, DRException {
        reportBuilder.toPdf(outputStream);
    }

    private void printForm(Page bean) {
        HorizontalListBuilder historyHeader = cmp.horizontalList().add(cmp.text(bean.getSubject())
                .setStyle(getReportStyles().getH3Style()));
        titleContent.add(historyHeader, getReportStyles().line(), cmp.verticalGap(10));
        titleContent.add(cmp.text(StringUtils.formatRichText(bean.getContent())));
    }

    private void printActivities(Page bean) {
        CommentService commentService = AppContextUtil.getSpringBean(CommentService.class);
        final CommentSearchCriteria commentCriteria = new CommentSearchCriteria();
        commentCriteria.setType(StringSearchField.and(ProjectTypeConstants.PAGE));
        commentCriteria.setTypeId(StringSearchField.and(bean.getPath()));
        final int commentCount = commentService.getTotalCount(commentCriteria);
        HorizontalListBuilder historyHeader = cmp.horizontalList().add(cmp.text("Comments (" + commentCount + ")")
                .setStyle(getReportStyles().getH3Style()));
        titleContent.add(historyHeader, getReportStyles().line(), cmp.verticalGap(10));

        List<SimpleComment> comments = (List<SimpleComment>) commentService.findPageableListByCriteria(new BasicSearchRequest<>(commentCriteria));
        Collections.sort(comments, dateComparator.reverse());
        for (SimpleComment activity : comments) {
            titleContent.add(buildCommentBlock(activity), cmp.verticalGap(10));
        }
    }

    private ComponentBuilder buildCommentBlock(SimpleComment comment) {
        TextFieldBuilder<String> authorField = cmp.text(StringUtils.trimHtmlTags(UserUIContext.getMessage(GenericI18Enum.EXT_ADDED_COMMENT, comment.getOwnerFullName(),
                UserUIContext.formatPrettyTime(comment.getCreatedtime())), Integer.MAX_VALUE)).setStyle(getReportStyles().getMetaInfoStyle());
        HorizontalListBuilder infoHeader = cmp.horizontalFlowList().add(authorField);
        return cmp.verticalList(infoHeader, cmp.text(StringUtils.trimHtmlTags(comment.getComment(), Integer.MAX_VALUE)))
                .setStyle(getReportStyles().getBorderStyle());
    }
}
