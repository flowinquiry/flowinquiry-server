package io.flowinquiry.health;

import io.flowinquiry.config.FlowInquiryProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class JWTSetupChecker implements ApplicationRunner {

    private static Logger LOG = LoggerFactory.getLogger(JWTSetupChecker.class);

    private final FlowInquiryProperties flowInquiryProperties;

    public JWTSetupChecker(FlowInquiryProperties flowInquiryProperties) {
        this.flowInquiryProperties = flowInquiryProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isEmpty(
                flowInquiryProperties
                        .getSecurity()
                        .getAuthentication()
                        .getJwt()
                        .getBase64Secret())) {
            throw new IllegalArgumentException("JWT secret is missing");
        } else {
            LOG.info("JWT secret found and ready to use");
        }
    }
}
