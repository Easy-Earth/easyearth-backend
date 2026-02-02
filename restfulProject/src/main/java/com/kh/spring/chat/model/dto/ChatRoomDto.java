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
public class ChatRoomDto {
    
    private Long chatRoomId;
    private String title;
    private String roomType; // SINGLE, GROUP
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;
    
    // Frontend 편의를 위한 추가 필드
    private int memberCount; // 참여 인원 수
    private String otherMemberName; // 1:1 채팅일 경우 상대방 이름
    private String otherMemberProfile; // 1:1 채팅일 경우 상대방 프로필
}
