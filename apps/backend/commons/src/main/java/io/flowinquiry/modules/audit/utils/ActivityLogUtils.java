package io.flowinquiry.modules.audit.utils;

import static j2html.TagCreator.each;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import j2html.tags.DomContent;
import java.util.List;

public class ActivityLogUtils {

    public static String generateHtmlLog(List<AuditUtils.FieldChange> changes) {
        DomContent htmlContent =
                table().with(
                                thead(tr(th("Field"), th("Old Value"), th("New Value"))),
                                tbody(
                                        each(
                                                changes,
                                                change ->
                                                        tr(
                                                                td(change.getFieldName()),
                                                                td(
                                                                        change.getOldValue() != null
                                                                                ? change.getOldValue()
                                                                                        .toString()
                                                                                : "N/A"),
                                                                td(
                                                                        change.getNewValue() != null
                                                                                ? change.getNewValue()
                                                                                        .toString()
                                                                                : "N/A")))));

        return htmlContent.render();
    }
}
