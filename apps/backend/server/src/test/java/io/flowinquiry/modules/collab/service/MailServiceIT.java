package io.flowinquiry.modules.collab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.flowinquiry.IntegrationTest;
import io.flowinquiry.config.FlowInquiryProperties;
import io.flowinquiry.modules.shared.Constants;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** Integration tests for {@link MailService}. */
@IntegrationTest
class MailServiceIT {

    private static final String[] languages = {"en"};
    private static final Pattern PATTERN_LOCALE_3 =
            Pattern.compile("([a-z]{2})-([a-zA-Z]{4})-([a-z]{2})");
    private static final Pattern PATTERN_LOCALE_2 = Pattern.compile("([a-z]{2})-([a-z]{2})");

    @Autowired private FlowInquiryProperties flowInquiryProperties;

    @MockitoBean private JavaMailSender javaMailSender;

    @Captor private ArgumentCaptor<MimeMessage> messageCaptor;

    @Autowired private MailService mailService;

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(
                            GreenMailConfiguration.aConfig()
                                    .withUser("noreply@flowinquiry.io", "user", "pass"))
                    .withPerMethodLifecycle(true);

    @BeforeEach
    public void setup() {
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void testSendEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false, false);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getSubject()).isEqualTo("testSubject");
        assertThat(receivedMessage.getContent()).isEqualTo("testContent");
        assertThat(receivedMessage.getContent()).isInstanceOf(String.class);
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString("john.doe@example.com");
        assertThat(receivedMessage.getDataHandler().getContentType())
                .isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    void testSendHtmlEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", false, true);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getSubject()).isEqualTo("testSubject");
        assertThat(receivedMessage.getContent()).isEqualTo("testContent");
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString("john.doe@example.com");
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent()).isInstanceOf(String.class);
        assertThat(receivedMessage.getDataHandler().getContentType())
                .isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    void testSendMultipartEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", true, false);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];

        MimeMultipart mp = (MimeMultipart) receivedMessage.getContent();
        MimeBodyPart part =
                (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
        ByteArrayOutputStream aos = new ByteArrayOutputStream();
        part.writeTo(aos);
        assertThat(receivedMessage.getSubject()).isEqualTo("testSubject");
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString("john.doe@example.com");
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent()).isInstanceOf(Multipart.class);
        assertThat(part.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    void testSendMultipartHtmlEmail() throws Exception {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", true, true);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];

        MimeMultipart mp = (MimeMultipart) receivedMessage.getContent();
        MimeBodyPart part =
                (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
        ByteArrayOutputStream aos = new ByteArrayOutputStream();
        part.writeTo(aos);
        assertThat(receivedMessage.getSubject()).isEqualTo("testSubject");
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString("john.doe@example.com");
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent()).isInstanceOf(Multipart.class);
        assertThat(part.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    void testSendActivationEmail() throws Exception {
        UserDTO user = new UserDTO();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setEmail("john.doe@example.com");
        mailService.sendActivationEmail(user);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString(user.getEmail());
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent().toString()).isNotEmpty();
        assertThat(receivedMessage.getDataHandler().getContentType())
                .isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    void testCreationEmail() throws Exception {
        UserDTO user = new UserDTO();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setEmail("john.doe@example.com");
        mailService.sendCreationEmail(user);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString(user.getEmail());
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent().toString()).isNotEmpty();
        assertThat(receivedMessage.getDataHandler().getContentType())
                .isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    void testSendPasswordResetMail() throws Exception {
        UserDTO user = new UserDTO();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setEmail("john.doe@example.com");
        mailService.sendPasswordResetMail(user);

        greenMail.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString(user.getEmail());
        assertThat(receivedMessage.getFrom()[0]).hasToString(mailService.getFrom());
        assertThat(receivedMessage.getContent().toString()).isNotEmpty();
        assertThat(receivedMessage.getDataHandler().getContentType())
                .isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    void testSendEmailWithException() {
        doThrow(MailSendException.class).when(javaMailSender).send(any(MimeMessage.class));
        try {
            mailService.sendEmail(
                    "john.doe@example.com", "testSubject", "testContent", false, false);
        } catch (Exception e) {
            fail("Exception shouldn't have been thrown");
        }
    }

    /** Convert a lang key to the Java locale. */
    private String getMessageSourceSuffixForLanguage(String langKey) {
        String javaLangKey = langKey;
        Matcher matcher2 = PATTERN_LOCALE_2.matcher(langKey);
        if (matcher2.matches()) {
            javaLangKey = matcher2.group(1) + "_" + matcher2.group(2).toUpperCase();
        }
        Matcher matcher3 = PATTERN_LOCALE_3.matcher(langKey);
        if (matcher3.matches()) {
            javaLangKey =
                    matcher3.group(1)
                            + "_"
                            + matcher3.group(2)
                            + "_"
                            + matcher3.group(3).toUpperCase();
        }
        return javaLangKey;
    }
}
