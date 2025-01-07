package io.flowinquiry.modules.usermanagement.web.rest;

import static io.flowinquiry.security.SecurityUtils.AUTHORITIES_KEY;
import static io.flowinquiry.security.SecurityUtils.JWT_ALGORITHM;
import static io.flowinquiry.security.SecurityUtils.USER_ID;

import io.flowinquiry.modules.usermanagement.InvalidLoginException;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import io.flowinquiry.modules.usermanagement.service.UserService;
import io.flowinquiry.modules.usermanagement.service.dto.FwUserDetails;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${flowinquiry.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${flowinquiry.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserService userService;

    private final UserRepository userRepository;

    public LoginController(
            JwtEncoder jwtEncoder,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            UserService userService,
            UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getEmail(), loginVM.getPassword());

        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDTO adminUserDTO =
                userService.getUserWithAuthorities().orElseThrow(InvalidLoginException::new);

        String jwt = this.createToken(authentication, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);

        // Update last login time for user
        userRepository.updateLastLoginTime(loginVM.getEmail(), LocalDateTime.now(ZoneOffset.UTC));
        return new ResponseEntity<>(adminUserDTO, httpHeaders, HttpStatus.OK);
    }

    private String createToken(Authentication authentication, boolean rememberMe) {
        String authorities =
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity;
        if (rememberMe) {
            validity = now.plus(this.tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
        } else {
            validity = now.plus(this.tokenValidityInSeconds, ChronoUnit.SECONDS);
        }

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuedAt(now)
                        .expiresAt(validity)
                        .subject(authentication.getName())
                        .claim(AUTHORITIES_KEY, authorities)
                        .claim(USER_ID, ((FwUserDetails) authentication.getPrincipal()).getUserId())
                        .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
