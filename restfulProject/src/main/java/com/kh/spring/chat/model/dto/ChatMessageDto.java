package com.kh.spring.chat.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    // 리액션 요약 정보
    @Builder.Default
    private List<ReactionSummary> reactions = new ArrayList<>();
    
    //내부 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionSummary {
        private String emojiType;
        private long count;
        private boolean selectedByMe;
    }
}
