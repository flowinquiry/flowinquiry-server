package io.flowinquiry.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

/** Custom handler for token authentication */
public class CustomBearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final BearerTokenAuthenticationEntryPoint delegate =
            new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException)
            throws IOException {

        // Add CORS headers
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        // Delegate to the default implementation
        delegate.commence(request, response, authException);

        // Optional: Add additional details in the response body
        if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            response.setContentType("application/json");
            response.getWriter()
                    .write(
                            "{\"error\": \"Unauthorized\", \"message\": \"Token expired or invalid.\"}");
        }
    }
}
