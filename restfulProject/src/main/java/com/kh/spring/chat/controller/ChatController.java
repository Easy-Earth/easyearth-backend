package com.kh.spring.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    /*
     * 클라이언트가 /app/chat/message 로 메시지를 보내면 이 메소드가 실행
     * 처리 후 /topic/chat/room/{roomId} 로 구독자들에게 메시지를 전송
     */
    @MessageMapping("/message")
    public void sendMessage(ChatMessageDto messageDto) {
        log.info("메시지 수신: {}", messageDto);
        
        // 1. DB에 메시지 저장
        ChatMessageDto savedMessage = chatService.saveMessage(messageDto);
        
        // 2. 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/room/" + messageDto.getChatRoomId(), savedMessage);
    }

    // ======================================================================
    // 2. REST API (Swagger에 노출됨)
    // ======================================================================

    @Operation(summary = "채팅방 목록 조회", description = "채팅방 목록을 조회합니다. memberId가 있으면 해당 회원이 참여한 방만 조회하고 안 읽은 메시지 수도 계산합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomList(@RequestParam(required = false) Long memberId) {
        return ResponseEntity.ok(chatService.selectChatRoomList(memberId));
    }

    @Operation(summary = "채팅방 참여", description = "채팅방에 참여합니다.")
    @PostMapping("/room/{roomId}/join")
    public ResponseEntity<Void> joinChatRoom(@PathVariable Long roomId, @RequestParam Long memberId) {
        chatService.joinChatRoom(roomId, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다.")
    @DeleteMapping("/room/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId, @RequestParam Long memberId) {
        chatService.leaveChatRoom(roomId, memberId);
        return ResponseEntity.ok().build();
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
    
    @Operation(summary = "채팅방 메시지 조회", description = "특정 채팅방의 메시지를 조회합니다.")
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getMessageList(
            @PathVariable Long roomId, 
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Long memberId) {
        return ResponseEntity.ok(chatService.selectMessageList(roomId, cursorId, memberId));
    }
    
    @Operation(summary = "메시지 읽음 처리", description = "특정 채팅방의 모든 메시지를 읽음 처리합니다 (마지막 읽은 메시지 ID 갱신).")
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long roomId, @RequestParam Long memberId, @RequestParam Long lastMessageId) {
        chatService.updateReadStatus(roomId, memberId, lastMessageId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "메시지 리액션(공감) 토글", description = "특정 메시지에 공감을 남기거나 취소/변경합니다.")
    @PostMapping("/message/{messageId}/reaction")
    public ResponseEntity<Void> toggleReaction(
            @PathVariable Long messageId, 
            @RequestParam Long memberId, 
            @RequestParam String emojiType) {
        chatService.toggleReaction(messageId, memberId, emojiType);
        return ResponseEntity.ok().build();
    }
}
