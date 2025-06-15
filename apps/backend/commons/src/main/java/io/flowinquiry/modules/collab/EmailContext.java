package io.flowinquiry.modules.collab;

import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.thymeleaf.context.Context;

/**
 * A utility class for building email contexts used in email template rendering. This class provides
 * a fluent API for setting up email templates with Thymeleaf, including recipient information,
 * template selection, subject line, and custom variables.
 */
public class EmailContext {
    /** The key used to store the base URL in the Thymeleaf context. */
    private static final String BASE_URL = "baseUrl";

    /** The recipient user of the email. */
    private UserDTO toUser;

    /** The Thymeleaf context used for template rendering. */
    private final Context thymeleafContext;

    /** The message source used for internationalization. */
    private final MessageSource messageSource;

    /** The locale used for internationalization. */
    private final Locale locale;

    /** The subject line of the email. */
    private String subject;

    /** The name of the template to be used for rendering the email. */
    private String templateName;

    /**
     * Constructs a new EmailContext with the specified locale, base URL, and message source.
     *
     * @param locale The locale to be used for internationalization
     * @param baseUrl The base URL of the application, used in email templates for links
     * @param messageSource The message source for retrieving localized messages
     */
    public EmailContext(Locale locale, String baseUrl, MessageSource messageSource) {
        this.locale = locale;
        thymeleafContext = new Context(locale);
        thymeleafContext.setVariable(BASE_URL, baseUrl);
        this.messageSource = messageSource;
    }

    /**
     * Sets the recipient user of the email and adds it to the Thymeleaf context.
     *
     * @param toUser The user who will receive the email
     * @return This EmailContext instance for method chaining
     */
    public EmailContext setToUser(UserDTO toUser) {
        this.toUser = toUser;
        thymeleafContext.setVariable("user", toUser);
        return this;
    }

    /**
     * Adds a variable to the Thymeleaf context for use in the email template.
     *
     * @param name The name of the variable to be used in the template
     * @param value The value of the variable
     * @return This EmailContext instance for method chaining
     */
    public EmailContext addVariable(String name, Object value) {
        thymeleafContext.setVariable(name, value);
        return this;
    }

    /**
     * Sets the template to be used for rendering the email.
     *
     * @param template The name of the template file (without extension)
     * @return This EmailContext instance for method chaining
     */
    public EmailContext setTemplate(String template) {
        this.templateName = template;
        return this;
    }

    /**
     * Sets the subject line of the email using the message source for internationalization.
     *
     * @param title The message key for the subject
     * @param args Optional arguments for the message
     * @return This EmailContext instance for method chaining
     */
    public EmailContext setSubject(String title, Object... args) {
        this.subject = messageSource.getMessage(title, args, locale);
        return this;
    }

    /**
     * Gets the recipient user of the email.
     *
     * @return The user who will receive the email
     */
    public UserDTO getToUser() {
        return toUser;
    }

    /**
     * Gets the subject line of the email.
     *
     * @return The subject line of the email
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the template name to be used for rendering the email.
     *
     * @return The name of the template file
     */
    public String getTemplate() {
        return templateName;
    }

    /**
     * Gets the Thymeleaf context used for template rendering.
     *
     * @return The Thymeleaf context with all variables
     */
    public Context getThymeleafContext() {
        return thymeleafContext;
    }
}
