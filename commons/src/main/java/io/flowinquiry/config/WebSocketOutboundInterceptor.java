package io.flowinquiry.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketOutboundInterceptor implements ChannelInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketOutboundInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
        if (accessor != null) {
            String destination = accessor.getDestination();
            LOG.debug("📤 Message Sent to WebSocket Broker: " + destination);
            LOG.debug("📝 Message Payload: " + message.getPayload());
        }
        return message;
    }
}
