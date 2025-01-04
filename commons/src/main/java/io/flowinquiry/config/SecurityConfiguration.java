package io.flowinquiry.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.flowinquiry.modules.usermanagement.AuthoritiesConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authz ->
                                // prettier-ignore
                                authz.requestMatchers(
                                                mvc.pattern(HttpMethod.POST, "/api/login"),
                                                mvc.pattern(HttpMethod.POST, "/api/authenticate"),
                                                mvc.pattern(HttpMethod.GET, "/api/authenticate"),
                                                mvc.pattern("/api/register"),
                                                mvc.pattern("/api/files/**"),
                                                mvc.pattern("/api/activate"),
                                                mvc.pattern("/api/account/reset-password/init"),
                                                mvc.pattern("/api/account/reset-password/finish"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/admin/**"))
                                        .hasAuthority(AuthoritiesConstants.ADMIN)
                                        .requestMatchers(mvc.pattern("/api/**"))
                                        .authenticated()
                                        .requestMatchers(mvc.pattern("/management/**"))
                                        .hasAuthority(
                                                AuthoritiesConstants.ADMIN)) // Enforces ROLE_ADMIN
                .httpBasic(withDefaults()) // Enable Basic Authentication
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(withDefaults())) // Enable OAuth2 Resource Server
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exceptions ->
                                exceptions
                                        .authenticationEntryPoint(
                                                new CustomBearerTokenAuthenticationEntryPoint())
                                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
