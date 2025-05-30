package io.flowinquiry.config;

import io.flowinquiry.modules.shared.service.dto.EditionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties(prefix = "flowinquiry")
@Validated
@Getter
public class FlowInquiryProperties {

    @Setter @NotNull private String version;

    @Setter @NotNull private EditionType edition;

    private final Http http = new Http();

    private final Security security = new Security();

    private final CorsConfiguration cors = new CorsConfiguration();

    private final Slack slack = new Slack();

    @Getter
    public static class Http {
        private final Cache cache = new Cache();

        @Getter
        @Setter
        public static class Cache {
            private int timeToLiveInDays = 1461;
        }
    }

    @Getter
    public static class Security {
        private final Authentication authentication = new Authentication();

        @Getter
        public static class Authentication {
            final Jwt jwt = new Jwt();

            @Getter
            @Setter
            public static class Jwt {
                private String base64Secret;
                private long tokenValidityInSeconds;
                private long tokenValidityInSecondsForRememberMe;
            }
        }
    }

    @Getter
    @Setter
    public static class Slack {
        private String token = "";
    }
}
