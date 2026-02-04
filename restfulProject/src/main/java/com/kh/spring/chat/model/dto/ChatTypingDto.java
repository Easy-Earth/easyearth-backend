package com.kh.spring.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTypingDto {
    private Long chatRoomId;
    private Long senderId;
    private boolean isTyping; // true: 입력중, false: 멈춤
}
