package com.kh.spring.chat.event;

import org.springframework.context.ApplicationEvent;

import com.kh.spring.chat.model.dto.ChatNotificationDto;

import lombok.Getter;

@Getter
public class ChatInvitationEvent {

    private final Long targetMemberId;
    private final ChatNotificationDto notification;

    public ChatInvitationEvent(Long targetMemberId, ChatNotificationDto notification) {
        this.targetMemberId = targetMemberId;
        this.notification = notification;
    }
}
