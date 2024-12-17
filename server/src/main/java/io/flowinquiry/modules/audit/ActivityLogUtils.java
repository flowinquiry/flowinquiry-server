package io.flowinquiry.modules.audit;

import static j2html.TagCreator.*;

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
