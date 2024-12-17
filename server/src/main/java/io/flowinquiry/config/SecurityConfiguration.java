package io.flowinquiry.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.flowinquiry.modules.usermanagement.AuthoritiesConstants;
import io.flowinquiry.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
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
                .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authz ->
                                // prettier-ignore
                                authz.requestMatchers(
                                                mvc.pattern("/index.html"),
                                                mvc.pattern("/*.js"),
                                                mvc.pattern("/*.txt"),
                                                mvc.pattern("/*.json"),
                                                mvc.pattern("/*.map"),
                                                mvc.pattern("/*.css"))
                                        .permitAll()
                                        .requestMatchers(
                                                mvc.pattern("/*.ico"),
                                                mvc.pattern("/*.png"),
                                                mvc.pattern("/*.svg"),
                                                mvc.pattern("/*.webapp"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/app/**"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/i18n/**"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/content/**"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/login"))
                                        .permitAll()
                                        .requestMatchers(
                                                mvc.pattern(HttpMethod.POST, "/api/authenticate"))
                                        .permitAll()
                                        .requestMatchers(
                                                mvc.pattern(HttpMethod.GET, "/api/authenticate"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/register"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/files/**"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/activate"))
                                        .permitAll()
                                        .requestMatchers(
                                                mvc.pattern("/api/account/reset-password/init"))
                                        .permitAll()
                                        .requestMatchers(
                                                mvc.pattern("/api/account/reset-password/finish"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/api/admin/**"))
                                        .hasAuthority(AuthoritiesConstants.ADMIN)
                                        .requestMatchers(mvc.pattern("/api/**"))
                                        .authenticated()
                                        .requestMatchers(mvc.pattern("/management/health"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/management/health/**"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/management/info"))
                                        .permitAll()
                                        .requestMatchers(mvc.pattern("/management/prometheus"))
                                        .permitAll()
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
                                                new BearerTokenAuthenticationEntryPoint())
                                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
