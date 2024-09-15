package com.crud.RankinDigitalCrud.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.crud.RankinDigitalCrud.Entity.ChatMessage;
import com.crud.RankinDigitalCrud.Service.ChatService;
import com.crud.RankinDigitalCrud.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register module for Java 8 date/time
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            JsonNode jsonNode = objectMapper.readTree(payload);

            String sender = jsonNode.has("sender") ? jsonNode.get("sender").asText() : null;
            String receiver = jsonNode.has("receiver") ? jsonNode.get("receiver").asText() : null;
            String text = jsonNode.has("message") ? jsonNode.get("message").asText() : null;
            boolean broadcast = jsonNode.has("broadcast") && jsonNode.get("broadcast").asBoolean();

            if (sender == null || (receiver == null && !broadcast) || text == null) {
                throw new IllegalArgumentException("Invalid message format");
            }

            ChatMessage chatMessage;
            if (broadcast) {
                // Save the broadcast message
                chatMessage = chatService.sendBroadcastMessage(sender, text);
                // Broadcast message to all sessions
                for (WebSocketSession wsSession : sessions.values()) {
                    if (wsSession.isOpen() && !wsSession.getAttributes().get("username").equals(sender)) {
                        wsSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                    }
                }
            } else {
                // Save the direct message
                chatMessage = chatService.sendMessage(sender, receiver, text);
                // Send the message to the intended receiver
                WebSocketSession receiverSession = sessions.get(receiver);
                if (receiverSession != null && receiverSession.isOpen()) {
                    receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                } else {
                    System.out.println("Receiver session not found or closed: " + receiver);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromUrl(session);
        if (token == null || !jwtUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String fullName = jwtUtil.extractFullName(token); // Extract full name from the token
        sessions.put(fullName, session);
        session.getAttributes().put("username", fullName); // Save full name in session attributes
        System.out.println("Session added for user: " + fullName);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessions.remove(username);
            System.out.println("Session removed for user: " + username);
        } else {
            System.out.println("No username found in session attributes.");
        }
    }

    private String extractTokenFromUrl(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("token=")) {
            return query.substring("token=".length());
        }
        return null;
    }
}
