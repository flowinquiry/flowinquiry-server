package io.flowinquiry.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.flowinquiry.modules.usermanagement.AuthoritiesConstants;
import io.flowinquiry.modules.usermanagement.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.ObjectPostProcessor;
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

    private final ObjectPostProcessor<Object> objectPostProcessor; // ✅ Fix bean conflict

    public SecurityConfiguration(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, MvcRequestMatcher.Builder mvc, OAuth2UserService oauth2UserService)
            throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authz ->
                                authz.requestMatchers(
                                                mvc.pattern(
                                                        HttpMethod.POST,
                                                        "/api/login"), // Username/password login
                                                mvc.pattern(HttpMethod.GET, "/login"),
                                                mvc.pattern(HttpMethod.POST, "/api/authenticate"),
                                                mvc.pattern(HttpMethod.GET, "/api/authenticate"),
                                                mvc.pattern("/api/register"),
                                                mvc.pattern("/api/files/**"),
                                                mvc.pattern("/api/activate"),
                                                mvc.pattern("/api/account/reset-password/init"),
                                                mvc.pattern("/api/account/reset-password/finish"),
                                                mvc.pattern("/oauth2/**"),
                                                mvc.pattern(
                                                        "/api/auth/**")) // OAuth2 login endpoints
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/admin/**"))
                                        .hasAuthority(AuthoritiesConstants.ADMIN)
                                        .requestMatchers(mvc.pattern("/api/**"))
                                        .authenticated()
                                        .requestMatchers("/management/**")
                                        .hasAuthority(AuthoritiesConstants.ADMIN))
                .oauth2Login(
                        oauth2 ->
                                oauth2.defaultSuccessUrl("/home", true)
                                        .userInfoEndpoint(
                                                userInfo ->
                                                        userInfo.userService(
                                                                oauth2UserService))) // Custom
                // OAuth2UserService for mapping users
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
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
