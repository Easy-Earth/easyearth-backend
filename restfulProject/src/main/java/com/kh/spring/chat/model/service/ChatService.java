package com.kh.spring.chat.model.service;

import java.util.List;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.dto.ChatMemberDto;

public interface ChatService {

    // 채팅방 목록 조회
    List<ChatRoomDto> selectChatRoomList(Long memberId);
    
    // 채팅방 참여
    void joinChatRoom(Long roomId, Long memberId);

    // 채팅방 나가기
    void leaveChatRoom(Long roomId, Long memberId);

    // 채팅방 생성
    ChatRoomDto createChatRoom(ChatRoomDto roomDto);

    // 채팅방 상세 조회
    ChatRoomDto selectChatRoom(Long roomId, Long memberId);

    // 메시지 저장
    ChatMessageDto saveMessage(ChatMessageDto messageDto);

    // 채팅방 메시지 내역 조회 (페이징)
    List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId, Long memberId, int limit);
    
    // 메시지 읽음 처리
    void updateReadStatus(Long roomId, Long memberId, Long lastMessageId);
    
    // 메시지 리액션 토글
    void toggleReaction(Long messageId, Long memberId, String emojiType);
    
    // 권한 변경 (방장 위임 등)
    void updateRole(Long chatRoomId, Long targetMemberId, Long requesterId, String newRole);

    // 멤버 강퇴
    void kickMember(Long chatRoomId, Long targetMemberId, Long requesterId);
    
    // 메시지 삭제 (Soft Delete) — requesterId: 방장 권한 삭제 시 전달
    void softDeleteMessage(Long messageId, Long memberId, Long requesterId);
    
    // 채팅방 공지 관리 (설정/해제)
    void setNotice(Long roomId, Long memberId, Long messageId);
    void clearNotice(Long roomId, Long memberId);
    
    // 메시지 검색 (키워드, 페이징)
    List<ChatMessageDto> searchMessages(Long chatRoomId, Long memberId, String keyword, int limit, int offset);
    
    // 글로벌 알림 전송 (비동기)
    void sendGlobalNotifications(ChatMessageDto savedMessage);
    
    // 채팅방 즐겨찾기 토글
    void toggleFavorite(Long roomId, Long memberId);
    
    // 사용자 초대
    void inviteUser(Long roomId, Long invitedMemberId, Long requesterId);
    
    // 초대 수락
    void acceptInvitation(Long roomId, Long memberId);
    
    // 초대 거절
    void rejectInvitation(Long roomId, Long memberId);
    
    // 프로필 이미지 변경 (채팅 전용)
    void updateProfile(Long memberId, String profileImageUrl);

    // 채팅방 멤버 목록 조회
    List<ChatMemberDto> getChatRoomMembers(Long chatRoomId);
    
    // 회원 검색 (이름/닉네임)
    List<ChatMemberDto> searchMember(String keyword);

    // 채팅방 이름 변경 (방장 전용)
    void updateRoomTitle(Long roomId, Long memberId, String newTitle);

    // 채팅방 이미지 변경 (방장 전용)
    void updateRoomImage(Long roomId, Long memberId, String imageUrl);

    // 초대 중인 사용자 목록 조회
    List<ChatMemberDto> getInvitedUsers(Long chatRoomId);

    // 초대 취소 (회수)
    void cancelInvitation(Long chatRoomId, Long targetMemberId, Long requesterId);
}
