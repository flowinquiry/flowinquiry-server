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
    private final WebSocketOutboundInterceptor webSocketOutboundInterceptor;

    public WebSocketConfiguration(
            JwtChannelInterceptor jwtChannelInterceptor,
            WebSocketOutboundInterceptor webSocketOutboundInterceptor) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
        this.webSocketOutboundInterceptor = webSocketOutboundInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/fiws").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
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
                .simpDestMatchers("/user/**")
                .authenticated() // ✅ Allow authenticated users
                .simpDestMatchers("/queue/**")
                .authenticated() // ✅ Secure message queues
                .simpDestMatchers("/app/**")
                .authenticated() // ✅ Secure app destinations
                .anyMessage()
                .authenticated()
                .build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor); // ✅ Register STOMP message interceptor
        registration.interceptors(
                webSocketOutboundInterceptor); // ✅ Log all outgoing messages, should use on dev
        // mode only
    }

    @Bean("csrfChannelInterceptor")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ChannelInterceptor noopCsrfChannelInterceptor() {
        return new ChannelInterceptor() {
            // No CSRF handling, acts as a no-op interceptor
        };
    }
}
