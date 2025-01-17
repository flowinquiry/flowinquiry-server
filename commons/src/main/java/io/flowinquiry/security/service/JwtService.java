package io.flowinquiry.security.service;

import static io.flowinquiry.security.SecurityUtils.AUTHORITIES_KEY;
import static io.flowinquiry.security.SecurityUtils.JWT_ALGORITHM;
import static io.flowinquiry.security.SecurityUtils.USER_ID;

import io.flowinquiry.modules.usermanagement.service.dto.FwUserDetails;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;

    @Value("${flowinquiry.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Authentication authentication) {
        return generateToken(
                ((FwUserDetails) authentication.getPrincipal()).getUserId(),
                authentication.getName(),
                authentication.getAuthorities());
    }

    public String generateToken(
            Long userId, String email, Collection<? extends GrantedAuthority> grantedAuthorities) {
        String authorities =
                grantedAuthorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuedAt(now)
                        .expiresAt(validity)
                        .subject(email)
                        .claim(AUTHORITIES_KEY, authorities)
                        .claim(USER_ID, userId)
                        .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
