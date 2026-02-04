package com.kh.spring.chat.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CHAT_ROOM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomEntity {
	


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ROOM_ID")
    private Long id;

    @Column(name = "TITLE", length = 100)
    private String title;

    @Column(name = "ROOM_TYPE", nullable = false, length = 20)
    private String roomType;

    @Lob
    @Column(name = "LAST_MESSAGE_CONTENT")
    private String lastMessageContent;

    @Column(name = "LAST_MESSAGE_AT")
    private LocalDateTime lastMessageAt;

    @lombok.Builder.Default
    @Column(name = "TOTAL_MESSAGE_COUNT", columnDefinition = "NUMBER DEFAULT 0")
    private Long totalMessageCount = 0L;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    
    // [CASCADE] 채팅방 삭제 시 메시지도 함께 삭제
    @lombok.Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private java.util.List<ChatMessageEntity> messages = new java.util.ArrayList<>();
    
    // [동시성 제어] 낙관적 락으로 totalMessageCount 정합성 보장
    @Version
    @Column(name = "VERSION")
    private Long version;
    
    public void updateLastMessage(String content, LocalDateTime at) {
        this.lastMessageContent = content;
        this.lastMessageAt = at;
        this.totalMessageCount = (this.totalMessageCount == null ? 0 : this.totalMessageCount) + 1;
    }
}