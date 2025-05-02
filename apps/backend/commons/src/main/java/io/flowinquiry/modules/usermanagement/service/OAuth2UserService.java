package io.flowinquiry.modules.usermanagement.service;

import io.flowinquiry.modules.usermanagement.AuthoritiesConstants;
import io.flowinquiry.modules.usermanagement.domain.Authority;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.domain.UserAuth;
import io.flowinquiry.modules.usermanagement.repository.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final Set<String> providers = Set.of("Google", "Github");

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String clientName = userRequest.getClientRegistration().getClientName();
        if (!providers.contains(clientName)) {
            throw new OAuth2AuthenticationException("Invalid client registration " + clientName);
        }

        // Extract user info from OAuth2 provider
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 response");
        }

        // Check if user exists; if not, register them
        User user =
                userRepository
                        .findOneWithAuthoritiesAndUserAuthsByEmailIgnoreCase(email)
                        .orElseGet(
                                () -> {
                                    // User is signing up
                                    User newUser = new User();
                                    newUser.setEmail(email);
                                    newUser.setAuthorities(
                                            Set.of(
                                                    Authority.builder()
                                                            .name(AuthoritiesConstants.ADMIN)
                                                            .build()));

                                    UserAuth userAuth = new UserAuth();
                                    userAuth.setAuthProvider(clientName);
                                    userAuth.setProviderUserId(email);
                                    newUser.setUserAuths(Set.of(userAuth));

                                    return userRepository.save(newUser);
                                });

        return new DefaultOAuth2User(
                user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .collect(Collectors.toSet()),
                oAuth2User.getAttributes(),
                "email");
    }
}
