package io.flowinquiry.modules.collab;

import io.flowinquiry.config.SpringContextProvider;
import io.flowinquiry.modules.collab.service.MailService;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.thymeleaf.context.Context;

public class EmailContext {
    private static final String BASE_URL = "baseUrl";

    private UserDTO toUser;
    private final Context thymeleafContext;
    private final MessageSource messageSource;
    private final Locale locale;
    private String subject;
    private String templateName;

    public EmailContext(Locale locale) {
        this.locale = locale;
        thymeleafContext = new Context(locale);

        MailService mailService = SpringContextProvider.getContext().getBean(MailService.class);
        messageSource = SpringContextProvider.getContext().getBean(MessageSource.class);
        thymeleafContext.setVariable(BASE_URL, mailService.getBaseUrl());
    }

    public EmailContext setToUser(UserDTO toUser) {
        this.toUser = toUser;
        thymeleafContext.setVariable("user", toUser);
        return this;
    }

    public EmailContext addVariable(String name, Object value) {
        thymeleafContext.setVariable(name, value);
        return this;
    }

    public EmailContext setTemplate(String template) {
        this.templateName = template;
        return this;
    }

    public EmailContext setSubject(String title) {
        return setSubject(title, (Object) null);
    }

    public EmailContext setSubject(String title, Object... args) {
        this.subject = messageSource.getMessage(title, args, locale);
        return this;
    }

    public UserDTO getToUser() {
        return toUser;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return templateName;
    }

    public Context getThymeleafContext() {
        return thymeleafContext;
    }
}
