package com.kh.spring.chat.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatNotificationDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.dto.ChatTypingDto;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatRoomUserEntity;
import com.kh.spring.util.ChatFileUtil;
import java.util.Arrays;

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
    private final com.kh.spring.chat.model.repository.ChatRoomUserRepository chatRoomUserRepository;
    private final com.kh.spring.chat.model.repository.MemberRepository memberRepository;

    @Operation(summary = "회원 검색 (Login ID)", description = "로그인 ID로 회원을 검색합니다.")
    @GetMapping("/users/search")
    public ResponseEntity<java.util.Map<String, Object>> searchMember(@RequestParam String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(member -> {
                    java.util.Map<String, Object> response = new java.util.HashMap<>();
                    response.put("memberId", member.getId());
                    response.put("name", member.getName());
                    response.put("loginId", member.getLoginId());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ======================================================================
    // 1. 실시간 채팅 (WebSocket/STOMP)
    // ======================================================================
    
    /*
     * 클라이언트가 /app/chat/message 로 메시지를 보내면 이 메소드가 실행
     * 처리 후 /topic/chat/room/{roomId} 로 구독자들에게 메시지를 전송
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageDto messageDto) {
        log.info("메시지 수신: {}", messageDto);
        
    
    // 1. DB에 메시지 저장 (트랜잭션 처리)
    ChatMessageDto savedMessage = chatService.saveMessage(messageDto);
    log.info("✅ 저장된 메시지 - messageId: {}, unreadCount: {}", 
        savedMessage.getMessageId(), savedMessage.getUnreadCount());
    
    // 2. 구독자들에게 메시지 전송 (채팅방 안)
    messagingTemplate.convertAndSend("/topic/chat/room/" + messageDto.getChatRoomId(), savedMessage);
        
        // 3. 글로벌 알림 전송 (Service에서 비동기 처리, 트랜잭션 경계 분리)
        chatService.sendGlobalNotifications(savedMessage);
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
    
    // ===================================
    // 3. 파일 업로드 (멀티미디어)
    // ===================================
    
    private final ChatFileUtil chatFileUtil; 

    @Operation(summary = "채팅 파일 업로드", description = "이미지/파일을 업로드하고 URL을 반환받습니다. (저장위치: /uploadFiles/chat/message/)")
    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // [보안] 파일 크기 검증 (10MB 제한)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("파일 크기는 10MB를 초과할 수 없습니다");
            }
            
            // [보안] 파일명 검증
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest().body("파일명이 유효하지 않습니다");
            }
            
            // [보안] 확장자 존재 여부 검증
            if (!originalFilename.contains(".")) {
                return ResponseEntity.badRequest().body("파일 확장자가 필요합니다");
            }
            
            // [보안] 파일 확장자 검증
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            List<String> allowedExts = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".pdf", ".txt", ".zip");
            if (!allowedExts.contains(ext)) {
                return ResponseEntity.badRequest().body("허용되지 않는 파일 확장자입니다");
            }
            
            // [보안] MIME 타입 검증 (확장자 위장 방지)
            String contentType = file.getContentType();
            if (contentType == null) {
                return ResponseEntity.badRequest().body("파일 타입을 확인할 수 없습니다");
            }
            
            List<String> allowedMimes = Arrays.asList(
                "image/jpeg", "image/png", "image/gif",  // 이미지
                "application/pdf",                        // PDF
                "text/plain",                             // 텍스트
                "application/zip", "application/x-zip-compressed"  // ZIP
            );
            
            if (!allowedMimes.contains(contentType)) {
                return ResponseEntity.badRequest().body("허용되지 않는 파일 형식입니다 (MIME: " + contentType + ")");
            }
            
        	// "chat/message" 폴더에 저장
            String savedFileName = chatFileUtil.saveFile(file, "chat/message");
            
            // 접근 가능한 URL 반환 (/chat/file/message/파일명)
            String fileUrl = "/chat/file/message/" + savedFileName;
            
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            log.error("File Upload Failed", e);
            return ResponseEntity.internalServerError().body("Upload Failed");
        }
    }
    
    // ===================================
    // 5. 그룹 관리 (Role & Kick)
    // ===================================

    @Operation(summary = "채팅방 권한 변경 (방장 위임)", description = "방장이 다른 멤버에게 방장을 위임하거나 권한을 변경합니다.")
    @PatchMapping("/room/{roomId}/user/{memberId}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long roomId,
            @PathVariable Long memberId,
            @RequestParam Long requesterId,
            @RequestParam String newRole) {
        chatService.updateRole(roomId, memberId, requesterId, newRole);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "멤버 강퇴", description = "방장 또는 관리자가 멤버를 강퇴합니다.")
    @DeleteMapping("/room/{roomId}/user/{memberId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable Long roomId,
            @PathVariable Long memberId,
            @RequestParam Long requesterId) {
        chatService.kickMember(roomId, memberId, requesterId);
        return ResponseEntity.ok().build();
        }

    @Operation(summary = "메시지 검색", description = "채팅방 내 메시지를 키워드로 검색합니다 (최신순).")
    @GetMapping("/room/{roomId}/search")
    public ResponseEntity<List<ChatMessageDto>> searchMessages(
            @PathVariable Long roomId,
            @RequestParam Long memberId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(chatService.searchMessages(roomId, memberId, keyword));
    }

    // ===================================
    // 4. 입력 상태 표시 (Typing Indicator)
    // ===================================
    
    @MessageMapping("/chat/typing")
    public void typing(ChatTypingDto typingDto) {
        // 클라이언트 구독 경로: /topic/chat/room/{roomId}/typing
        messagingTemplate.convertAndSend("/topic/chat/room/" + typingDto.getChatRoomId() + "/typing", typingDto);
    }
    
    // ===================================
    // 6. 메시지 삭제 (Soft Delete)
    // ===================================
    
    @Operation(summary = "메시지 삭제", description = "작성자가 자신의 메시지를 삭제합니다. (Soft Delete)")
    @PutMapping("/message/{messageId}/delete")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId, 
            @RequestParam Long memberId) {
        chatService.softDeleteMessage(messageId, memberId);
        return ResponseEntity.ok().build();
    }
    
    // ===================================
    // 7. 채팅방 공지 관리
    // ===================================
    
    @Operation(summary = "채팅방 공지 설정", description = "방장 또는 관리자가 특정 메시지를 공지로 설정합니다.")
    @PostMapping("/room/{roomId}/notice")
    public ResponseEntity<Void> setNotice(
            @PathVariable Long roomId, 
            @RequestParam Long memberId,
            @RequestParam Long messageId) {
        chatService.setNotice(roomId, memberId, messageId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "채팅방 공지 해제", description = "방장 또는 관리자가 채팅방 공지를 해제합니다.")
    @PutMapping("/rooms/{roomId}/notice")
    public ResponseEntity<Void> clearNotice(
            @PathVariable Long roomId, 
            @RequestParam Long memberId) {
        chatService.clearNotice(roomId, memberId);
        return ResponseEntity.ok().build();
    }
    
    // [즐겨찾기] 채팅방 즐겨찾기 토글
    @PutMapping("/rooms/{roomId}/favorite")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long roomId,
            @RequestParam Long memberId) {
        chatService.toggleFavorite(roomId, memberId);
        return ResponseEntity.ok().build();
    }
    
    // [초대] 사용자 초대
    @PostMapping("/rooms/{roomId}/invite")
    public ResponseEntity<Void> inviteUser(
            @PathVariable Long roomId,
            @RequestParam Long invitedMemberId,
            @RequestParam Long requesterId) {
        chatService.inviteUser(roomId, invitedMemberId, requesterId);
        return ResponseEntity.ok().build();
    }
    
    // [초대] 초대 수락
    @PutMapping("/rooms/{roomId}/invitation/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable Long roomId,
            @RequestParam Long memberId) {
        chatService.acceptInvitation(roomId, memberId);
        return ResponseEntity.ok().build();
    }
    
    // [초대] 초대 거절
    @PutMapping("/rooms/{roomId}/invitation/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable Long roomId,
            @RequestParam Long memberId) {
        chatService.rejectInvitation(roomId, memberId);
        return ResponseEntity.ok().build();
    }
}
