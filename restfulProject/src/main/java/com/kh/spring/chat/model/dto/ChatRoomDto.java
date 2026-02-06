package com.kh.spring.chat.model.dto;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long creatorId; // 방 생성자 ID
    private Long targetMemberId; // 1:1 채팅 상대방 ID (생성 시 사용)
    private String title;
    private String roomType; // SINGLE, GROUP
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;
    
    // Frontend 편의를 위한 추가 필드
    private int memberCount; // 참여 인원 수
    private String otherMemberName; // 1:1 채팅일 경우 상대방 이름
    private String otherMemberProfile; // 1:1 채팅일 경우 상대방 프로필
    private int unreadCount; // 안 읽은 메시지 수
    
    // 공지 관련 필드
    private String noticeContent; // 공지 내용
    private Long noticeMessageId; // 공지로 설정된 메시지 ID
    private String roomName; // title 별칭 (프론트엔드 호환성)
    
    // 참여자 목록 (멤버 관리용)
    private List<ParticipantInfo> participants;
    
    // 즐겨찾기 여부
    private boolean isFavorite;
    
    // 초대 상태
    private String invitationStatus; // PENDING, ACCEPTED, REJECTED
    
    // 참여자 정보 내부 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantInfo {
        private Long memberId;
        private String memberName;
        private String name; // memberName과 같은 값 (프론트 호환성)
        private String loginId;
        private String profileImageUrl;
        private String role; // OWNER, ADMIN, MEMBER
        private LocalDateTime joinedAt;
    }
}
