package com.kh.spring.chat.event;

import lombok.Getter;

@Getter
public class ChatEvent {

    private final Long targetMemberId; // 알림 받을 대상 (null이면 topic/chat/room/{roomId}로 전송)
    private final String destination;  // 전송할 토픽 경로 (예: "/topic/user/" + memberId)
    private final Object payload;      // 전송할 데이터 (Dto, Map 등)

    public ChatEvent(String destination, Object payload) {
        this.targetMemberId = null;
        this.destination = destination;
        this.payload = payload;
    }
    
    // 특정 사용자에게 보낼 때 편의 생성자
    public ChatEvent(Long targetMemberId, Object payload) {
        this.targetMemberId = targetMemberId;
        this.destination = "/topic/user/" + targetMemberId;
        this.payload = payload;
    }
}
