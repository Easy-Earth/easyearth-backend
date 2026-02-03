package com.kh.spring.chat.model.service;

import java.util.List;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;

public interface ChatService {

    //채팅방 목록 조회 (memberId가 있으면 해당 회원이 참여한 방만 조회 + 안읽은 메시지 수)
    List<ChatRoomDto> selectChatRoomList(Long memberId);
    
    //채팅방 참여
    void joinChatRoom(Long roomId, Long memberId);

    //채팅방 나가기 (구독 취소 개념이 아닌 참여 목록에서 제거)
    void leaveChatRoom(Long roomId, Long memberId);

    //채팅방 생성
    ChatRoomDto createChatRoom(ChatRoomDto roomDto);

    //채팅방 입장 (상세 조회 check)
    ChatRoomDto selectChatRoom(Long roomId);

    //메시지 저장
    ChatMessageDto saveMessage(ChatMessageDto messageDto);

    //채팅방 메시지 내역 조회 (무한 스크롤)
    //roomId 채팅방 ID
    //cursorId 마지막으로 로드된 메시지 ID (첫 조회 시 null 가능)
    List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId);
    
    // 메시지 읽음 처리 (마지막 읽은 메시지 ID 업데이트)
    void updateReadStatus(Long roomId, Long memberId, Long lastMessageId);
}
