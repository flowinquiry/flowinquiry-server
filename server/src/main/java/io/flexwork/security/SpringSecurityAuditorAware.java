package io.flexwork.security;

import io.flexwork.modules.usermanagement.service.dto.UserKey;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/** Implementation of {@link AuditorAware} based on Spring Security. */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return SecurityUtils.getCurrentUserLogin().map(UserKey::getId).or(() -> Optional.of(-1L));
    }
}
