package com.kh.spring.chat.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String messageType; // TEXT, IMAGE, ENTER, LEAVE
    private LocalDateTime createdAt;
    
    // Frontend 편의를 위한 추가 필드
    private String senderName;
    private String senderProfileImage;
}
