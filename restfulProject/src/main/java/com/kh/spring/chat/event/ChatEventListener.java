package com.kh.spring.chat.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChatEvent(ChatEvent event) {
        log.info("🔔 [Transaction Committed] Sending WebSocket Event to: {}", event.getDestination());
        messagingTemplate.convertAndSend(event.getDestination(), event.getPayload());
    }
}
