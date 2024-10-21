package io.flexwork.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MaskingMessageConverterTest {
    private MaskingMessageConverter maskingMessageConverter = new MaskingMessageConverter();

    @Mock
    private ILoggingEvent mockLoggingEvent;

    @Test
    public void testMaskingEmail() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("User email is john.doe@example.com");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("User email is [ANONYMIZED_EMAIL]", maskedMessage);
    }

    @Test
    public void testMaskingCreditCard() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("User credit card number is 1234-5678-9876-5432");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("User credit card number is [ANONYMIZED_CREDIT_CARD]", maskedMessage);
    }

    @Test
    public void testMaskingPhoneNumber() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("User phone number is +1-123-456-7890");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("User phone number is [ANONYMIZED_PHONE]", maskedMessage);
    }

    @Test
    public void testMaskingIPAddress() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("User IP address is 192.168.1.1");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("User IP address is [ANONYMIZED_IP]", maskedMessage);
    }

    @Test
    public void testNoMaskingNeeded() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("This is a regular log message with no sensitive data");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("This is a regular log message with no sensitive data", maskedMessage);
    }

    @Test
    public void testMultipleSensitiveData() {
        when(mockLoggingEvent.getFormattedMessage()).thenReturn("User email is john.doe@example.com, credit card is 1234-5678-9876-5432");

        String maskedMessage = maskingMessageConverter.convert(mockLoggingEvent);

        assertEquals("User email is [ANONYMIZED_EMAIL], credit card is [ANONYMIZED_CREDIT_CARD]", maskedMessage);
    }
}
