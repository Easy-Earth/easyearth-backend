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
@lombok.Builder
@lombok.NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@lombok.AllArgsConstructor
public class ChatRoomUserEntity {
	


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

    @lombok.Builder.Default
    @Column(name = "LAST_READ_MESSAGE_COUNT", columnDefinition = "NUMBER DEFAULT 0")
    private Long lastReadMessageCount = 0L;

    @CreationTimestamp
    @Column(name = "JOINED_AT")
    private LocalDateTime joinedAt;

    @lombok.Builder.Default
    @Column(name = "ROLE", nullable = false, length = 20)
    private String role = "MEMBER"; // OWNER, ADMIN, MEMBER

    @lombok.Builder.Default
    @Column(name = "IS_FAVORITE", columnDefinition = "NUMBER(1) DEFAULT 0")
    private Integer isFavorite = 0; // 0: OFF, 1: ON

    @lombok.Builder.Default
    @Column(name = "INVITATION_STATUS", length = 20)
    private String invitationStatus = "ACCEPTED"; // PENDING, ACCEPTED, REJECTED

    public void updateLastReadMessageId(Long messageId) {
        this.lastReadMessageId = messageId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void updateLastReadMessageCount(Long count) {
        this.lastReadMessageCount = count;
    }

    public void toggleFavorite() {
        this.isFavorite = (this.isFavorite == 0) ? 1 : 0;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
