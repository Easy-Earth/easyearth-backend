package com.kh.spring.chat.model.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CHAT_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessageEntity {
	


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID", nullable = false)
    private MemberEntity sender;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "MESSAGE_TYPE", nullable = false, length = 20)
    private String messageType;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_MESSAGE_ID")
    private ChatMessageEntity parentMessage;
    
    @Builder.Default 
    @OneToMany(mappedBy = "chatMessage", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageReactionEntity> reactions = new ArrayList<>();
    //reactions = null 이 되지 않기 위헤 기본값 지정
    //mappedBy : 해당 정보는 chatMessage라는 필드가 지금의 컬럼과 연결되어있기에 거울처럼 비춰서 매핑 결과를 보여주는 기능
    //편의성 기능. MessageReactionEntity 에서 조회하는 것이 아닌 chatMessageEntity에서도 반대로 조회할 수 있게 해줌
}
