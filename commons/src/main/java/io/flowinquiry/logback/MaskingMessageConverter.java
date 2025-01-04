package io.flowinquiry.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Pattern;

public class MaskingMessageConverter extends ClassicConverter {

    // Define regex patterns for sensitive data
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("\\b\\d{4}-?\\d{4}-?\\d{4}-?\\d{4}\\b");
    private static final Pattern IP_ADDRESS_PATTERN =
            Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    private static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile(
                    "\\+?(\\d{1,4})?[-\\s.]?\\(?\\d{1,4}\\)?[-\\s.]?\\d{1,4}[-\\s.]?\\d{1,9}");

    @Override
    public String convert(ILoggingEvent event) {
        String originalMessage = event.getFormattedMessage();

        // Apply masking
        String maskedMessage = maskSensitiveData(originalMessage);

        return maskedMessage;
    }

    private String maskSensitiveData(String message) {
        // Mask IP addresses
        String maskedMessage = IP_ADDRESS_PATTERN.matcher(message).replaceAll("[ANONYMIZED_IP]");

        // Mask email addresses
        maskedMessage = EMAIL_PATTERN.matcher(maskedMessage).replaceAll("[ANONYMIZED_EMAIL]");
        // Mask credit card numbers
        maskedMessage =
                CREDIT_CARD_PATTERN.matcher(maskedMessage).replaceAll("[ANONYMIZED_CREDIT_CARD]");
        // Mask phone numbers
        maskedMessage =
                PHONE_NUMBER_PATTERN.matcher(maskedMessage).replaceAll("[ANONYMIZED_PHONE]");

        return maskedMessage;
    }
}
