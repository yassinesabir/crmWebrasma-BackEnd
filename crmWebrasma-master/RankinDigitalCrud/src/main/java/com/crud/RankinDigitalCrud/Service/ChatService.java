package com.crud.RankinDigitalCrud.Service;

import com.crud.RankinDigitalCrud.Entity.ChatMessage;
import com.crud.RankinDigitalCrud.Repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage sendMessage(String sender, String receiver, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setBroadcast(false); // This is a direct message
        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage sendBroadcastMessage(String sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(null); // Receiver is null for broadcast messages
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setBroadcast(true); // This is a broadcast message
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(String user1, String user2) {
        return chatMessageRepository.findBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1);
    }


    public List<ChatMessage> getBroadcastMessages() {
        return chatMessageRepository.findByBroadcast(true);
    }
}
