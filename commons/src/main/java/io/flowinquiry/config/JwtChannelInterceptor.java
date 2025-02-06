package io.flowinquiry.config;

import io.flowinquiry.security.service.JwtService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    private final JwtService jwtService;

    public JwtChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class);
        if (accessor != null) {
            Map nativeHeaders = (Map) accessor.getHeader("nativeHeaders");
            if (nativeHeaders != null) {
                List authorizationValues = ((List) nativeHeaders.get("Authorization"));
                if (authorizationValues != null && !authorizationValues.isEmpty()) {
                    String authHeader = (String) authorizationValues.getFirst();
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        Authentication authentication = jwtService.authenticateToken(token);
                        if (authentication != null) {
                            LOG.debug(
                                    "🔐 STOMP Message Authenticated for user: {}",
                                    authentication.getName());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
        return message;
    }
}
