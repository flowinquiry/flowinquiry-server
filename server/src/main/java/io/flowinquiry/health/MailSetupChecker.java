package io.flowinquiry.health;

import io.flowinquiry.config.FlowInquiryProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class MailSetupChecker implements ApplicationRunner {

    private static Logger LOG = LoggerFactory.getLogger(MailSetupChecker.class);

    private final FlowInquiryProperties flowInquiryProperties;

    public MailSetupChecker(FlowInquiryProperties flowInquiryProperties) {
        this.flowInquiryProperties = flowInquiryProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isEmpty(flowInquiryProperties.getMail().getBaseUrl())
                || !flowInquiryProperties.getMail().isEnabled()) {
            LOG.warn("Email provider is not configured yet");
        } else {
            LOG.info("Mail settings are found");
        }
    }
}
