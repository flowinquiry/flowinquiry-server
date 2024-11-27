package io.flexwork.modules.audit;

import static j2html.TagCreator.*;

import io.flexwork.modules.collab.domain.EntityType;
import j2html.tags.DomContent;
import java.util.List;

public class ActivityLogUtils {

    public static String generateHtmlLog(
            EntityType entityType, Long entityId, List<AuditUtils.FieldChange> changes) {
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

        return div(
                        h3("Activity Log for Entity: " + entityType + " (ID: " + entityId + ")"),
                        htmlContent)
                .render();
    }
}
