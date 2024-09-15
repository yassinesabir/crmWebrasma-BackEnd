package com.crud.RankinDigitalCrud.Controller;

import com.crud.RankinDigitalCrud.Entity.ChatMessage;
import com.crud.RankinDigitalCrud.Service.ChatService;
import com.crud.RankinDigitalCrud.Service.KeycloakAdminService;
import com.crud.RankinDigitalCrud.dto.UserDTO;
import com.crud.RankinDigitalCrud.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final KeycloakAdminService keycloakAdminService;

    public ChatController(ChatService chatService, JwtUtil jwtUtil, KeycloakAdminService keycloakAdminService) {
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
        this.keycloakAdminService = keycloakAdminService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestBody ChatMessage chatMessage,
            HttpServletRequest request) {

        String sender = jwtUtil.extractFullNameFromRequest(request);
        ChatMessage response = chatService.sendMessage(sender, chatMessage.getReceiver(), chatMessage.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2) {

        List<ChatMessage> chatHistory = chatService.getChatHistory(user1, user2);
        return ResponseEntity.ok(chatHistory);
    }

    @GetMapping("/broadcasts")
    public ResponseEntity<List<ChatMessage>> getBroadcastMessages() {
        List<ChatMessage> broadcastMessages = chatService.getBroadcastMessages();
        return ResponseEntity.ok(broadcastMessages);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(){
        List<UserDTO> users = keycloakAdminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
