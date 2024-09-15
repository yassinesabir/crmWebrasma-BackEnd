package com.crud.RankinDigitalCrud.Repository;

import com.crud.RankinDigitalCrud.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrSenderAndReceiver(String sender1, String receiver1, String sender2, String receiver2);
    List<ChatMessage> findByBroadcast(boolean broadcast);
}
