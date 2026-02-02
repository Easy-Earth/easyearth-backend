package com.kh.spring.chat.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "CHAT_ROOM_USER", indexes = {
        @Index(name = "IDX_ROOM_MEMBER_COMP", columnList = "CHAT_ROOM_ID, MEMBER_ID")
})
public class ChatRoomUserEntity {
	
	protected ChatRoomUserEntity() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ROOM_USER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoomEntity chatRoom; // 클래스 이름 변경 반영

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private MemberEntity member; // 클래스 이름 변경 반영

    @Column(name = "LAST_READ_MESSAGE_ID")
    private Long lastReadMessageId;

    @CreationTimestamp
    @Column(name = "JOINED_AT")
    private LocalDateTime joinedAt;
}
