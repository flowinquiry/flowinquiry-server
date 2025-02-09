package io.flowinquiry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.util.matcher.SimpMessageTypeMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketConfiguration(JwtChannelInterceptor jwtChannelInterceptor) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/fiws").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Bean
    public AuthorizationManager<Message<?>> authorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder builder) {
        return builder.matchers(new SimpMessageTypeMatcher(SimpMessageType.CONNECT))
                .permitAll() // ✅ Allow WebSocket CONNECT
                .matchers(new SimpMessageTypeMatcher(SimpMessageType.SUBSCRIBE))
                .permitAll() // ✅ Allow WebSocket SUBSCRIBE
                .matchers(new SimpMessageTypeMatcher(SimpMessageType.DISCONNECT))
                .permitAll() // ✅ Allow WebSocket DISCONNECT
                .simpMessageDestMatchers("/user/**")
                .authenticated() // ✅ Allow authenticated users
                .simpMessageDestMatchers("/queue/**")
                .authenticated() // ✅ Secure message queues
                .simpMessageDestMatchers("/app/**")
                .authenticated() // ✅ Secure app destinations
                .anyMessage()
                .denyAll()
                .build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor); // ✅ Register STOMP message interceptor
    }

    @Bean("csrfChannelInterceptor")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ChannelInterceptor noopCsrfChannelInterceptor() {
        return new ChannelInterceptor() {
            // No CSRF handling, acts as a no-op interceptor
        };
    }
}
