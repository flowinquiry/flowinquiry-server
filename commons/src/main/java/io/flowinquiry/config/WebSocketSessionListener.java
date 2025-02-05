package io.flowinquiry.config;

import java.util.Set;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketSessionListener {

    private final SimpUserRegistry simpUserRegistry;

    public WebSocketSessionListener(SimpUserRegistry simpUserRegistry) {
        this.simpUserRegistry = simpUserRegistry;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        System.out.println("✅ WebSocket Connected - Session ID: " + sessionId);
        logConnectedUsers();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        System.out.println("❌ WebSocket Disconnected - Session ID: " + sessionId);
        logConnectedUsers();
    }

    private void logConnectedUsers() {
        Set<SimpUser> users = simpUserRegistry.getUsers();
        System.out.println("👥 Active WebSocket Users: " + users.size());
        for (SimpUser user : users) {
            System.out.println("🔹 User: " + user.getName());
        }
    }
}
