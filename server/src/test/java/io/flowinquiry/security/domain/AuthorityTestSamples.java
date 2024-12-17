package io.flowinquiry.security.domain;

import io.flowinquiry.modules.usermanagement.domain.Authority;
import java.util.UUID;

public class AuthorityTestSamples {

    public static Authority getAuthoritySample1() {
        return Authority.builder().name("name1").descriptiveName("descriptive_name1").build();
    }

    public static Authority getAuthoritySample2() {
        return Authority.builder().name("name2").descriptiveName("descriptive_name2").build();
    }

    public static Authority getAuthorityRandomSampleGenerator() {
        return Authority.builder()
                .name(UUID.randomUUID().toString())
                .descriptiveName(UUID.randomUUID().toString())
                .build();
    }
}
