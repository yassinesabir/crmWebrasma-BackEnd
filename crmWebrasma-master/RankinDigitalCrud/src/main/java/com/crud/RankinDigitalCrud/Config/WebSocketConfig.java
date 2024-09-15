
package com.crud.RankinDigitalCrud.Config;

import com.crud.RankinDigitalCrud.Service.JwtTokenValidator;
import com.crud.RankinDigitalCrud.WebSocket.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final JwtTokenValidator jwtTokenValidator; // Token validator component

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler, JwtTokenValidator jwtTokenValidator) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("http://localhost:3000") // Allow frontend origin
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        String query = request.getURI().getQuery();
                        if (query != null && query.contains("token=")) {
                            String token = query.split("token=")[1];
                            System.out.println("Extracted Token: " + token); // Debugging
                            return jwtTokenValidator.validateToken(token); // Validate JWT
                        }
                        System.out.println("No token found or invalid format");
                        return false; // Reject handshake if no token or invalid token
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
                        // No operation after handshake
                    }
                });
    }
}
