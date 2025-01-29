package io.flowinquiry.utils;

import org.apache.commons.text.StringEscapeUtils;

public class StringUtils {

    public static String polishedHtmlTagsMessage(String message) {
        String preProcessedHtml = message.replaceAll(">(\\s*)<", "> <");
        return StringEscapeUtils.unescapeHtml4(preProcessedHtml).replaceAll("<[^>]+>", "");
    }
}
