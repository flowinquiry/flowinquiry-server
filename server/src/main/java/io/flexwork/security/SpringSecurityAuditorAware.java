package io.flexwork.security;

import io.flexwork.modules.usermanagement.service.dto.UserKey;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/** Implementation of {@link AuditorAware} based on Spring Security. */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return SecurityUtils.getCurrentUserLogin()
                .map(UserKey::getEmail)
                .or(() -> Optional.of(Constants.SYSTEM));
    }
}
