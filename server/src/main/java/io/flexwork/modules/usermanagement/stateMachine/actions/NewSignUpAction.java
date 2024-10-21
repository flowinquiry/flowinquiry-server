package io.flexwork.modules.usermanagement.stateMachine.actions;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.stateMachine.SignupEvents;
import io.flexwork.modules.usermanagement.stateMachine.SignupStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class NewSignUpAction implements Action<SignupStates, SignupEvents> {

    private static final Logger LOG = LoggerFactory.getLogger(NewSignUpAction.class);

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public NewSignUpAction(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void execute(StateContext<SignupStates, SignupEvents> context) {
        User user = (User) context.getExtendedState().getVariables().get("user");

        LOG.debug("Send email to the new user");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("haiphucnguyen@gmail.com");
        message.setSubject("Sign up");
        message.setFrom("noreply@flexwork.com");
        Context templateContext = new Context();
        templateContext.setVariable("user", user);
        String body = templateEngine.process("email_verification.html", templateContext);
        message.setText(body);
        mailSender.send(message);
    }
}
