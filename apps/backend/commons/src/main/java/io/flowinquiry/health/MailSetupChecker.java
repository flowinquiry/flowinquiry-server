package io.flowinquiry.health;

import io.flowinquiry.modules.collab.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class MailSetupChecker implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MailSetupChecker.class);

    private final MailService mailService;

    public MailSetupChecker(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (mailService.isMailEnabled()) {
            LOG.info("Mail settings are found");
        } else {
            LOG.warn("Email provider is not configured yet");
        }
    }
}
