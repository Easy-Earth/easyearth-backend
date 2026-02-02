package com.kh.spring.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat", description = "채팅 관련 API")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // ======================================================================
    // 1. 실시간 채팅 (WebSocket/STOMP)
    // ======================================================================
    
    /**
     * 클라이언트가 /app/chat/message 로 메시지를 보내면 이 메소드가 실행됩니다.
     * 처리 후 /topic/chat/room/{roomId} 로 구독자들에게 메시지를 전송합니다.
     */
    @MessageMapping("/message") // 실제 라우팅: /app/chat/message (WebSocketConfig prefix 영향 받음)
    public void sendMessage(ChatMessageDto messageDto) {
        log.info("메시지 수신: {}", messageDto);
        
        // 1. DB에 메시지 저장
        ChatMessageDto savedMessage = chatService.saveMessage(messageDto);
        
        // 2. 구독자들에게 메시지 전송
        // 구독 주소: /topic/chat/room/{roomId}
        messagingTemplate.convertAndSend("/topic/chat/room/" + messageDto.getChatRoomId(), savedMessage);
    }

    // ======================================================================
    // 2. REST API (Swagger에 노출됨)
    // ======================================================================

    @Operation(summary = "채팅방 목록 조회", description = "개설된 모든 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList() {
        return ResponseEntity.ok(chatService.selectChatRoomList());
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping("/room")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto roomDto) {
        return ResponseEntity.ok(chatService.createChatRoom(roomDto));
    }

    @Operation(summary = "채팅방 상세/입장", description = "특정 채팅방의 정보를 조회합니다.")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomDto> getChatRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.selectChatRoom(roomId));
    }
    
    @Operation(summary = "이전 채팅 내역 조회 (무한 스크롤)", description = "특정 채팅방의 이전 대화 내역을 불러옵니다. cursorId를 보내면 그 이전 메시지를 50개 가져옵니다.")
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessageList(
            @PathVariable Long roomId, 
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long cursorId) {
        return ResponseEntity.ok(chatService.selectMessageList(roomId, cursorId));
    }
}
