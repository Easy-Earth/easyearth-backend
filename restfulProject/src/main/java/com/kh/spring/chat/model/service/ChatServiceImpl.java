package com.kh.spring.chat.model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.repository.ChatMessageRepository;
import com.kh.spring.chat.model.repository.ChatRoomRepository;
import com.kh.spring.chat.model.repository.ChatRoomUserRepository;
import com.kh.spring.chat.model.repository.MemberRepository;
import com.kh.spring.chat.model.vo.ChatMessageEntity;
import com.kh.spring.chat.model.vo.ChatRoomEntity;
import com.kh.spring.chat.model.vo.ChatRoomUserEntity;
import com.kh.spring.chat.model.vo.MemberEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    //MemberId를 이용한 채팅방 목록(리스트) 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> selectChatRoomList(Long memberId) {
        List<ChatRoomEntity> chatRooms;
        
        if (memberId != null) {
            // 내가 참여한 방 목록만 조회
            chatRooms = chatRoomRepository.findChatRoomsByMemberId(memberId);
        } else {
            //에러 페이지로
            throw new IllegalArgumentException("Member not found");
        }

        return chatRooms.stream()
                .map(entity -> {
                    int unreadCount = 0;
                    if (memberId != null) {
                        // 안 읽은 메시지 수 계산
                        unreadCount = countUnreadMessages(entity.getId(), memberId);
                    }
                    
                    return ChatRoomDto.builder()
                            .chatRoomId(entity.getId())
                            .title(entity.getTitle())
                            .roomType(entity.getRoomType())
                            .lastMessageContent(entity.getLastMessageContent())
                            .lastMessageAt(entity.getLastMessageAt())
                            .unreadCount(unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    //안 읽은 메시지 갯수
    private int countUnreadMessages(Long chatRoomId, Long memberId) {
        // 1. 사용자의 마지막 읽은 메시지 ID 조회
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElse(null);

        if (roomUser == null) {
            return 0;
        }

        Long lastReadMessageId = roomUser.getLastReadMessageId();
        if (lastReadMessageId == null) {
            lastReadMessageId = 0L;
        }

        // 2. 그 이후의 메시지 개수 카운트
        return (int) chatMessageRepository.countByChatRoomIdAndIdGreaterThan(chatRoomId, lastReadMessageId);
    }

    @Override
    public ChatRoomDto createChatRoom(ChatRoomDto roomDto) {
        // 1. 채팅방 생성(db 저장)
        ChatRoomEntity entity = ChatRoomEntity.builder()
                .title(roomDto.getTitle())
                .roomType(roomDto.getRoomType())
                .createdAt(LocalDateTime.now())
                .build();
        
        ChatRoomEntity saved = chatRoomRepository.save(entity);
        
        // 2. 개설자를 참여자(ChatRoomUser)로 추가
        if (roomDto.getCreatorId() != null) {
            MemberEntity creator = memberRepository.findById(roomDto.getCreatorId())
                    .orElseThrow(() -> new IllegalArgumentException("Creator not found"));

            //매핑 엔티티 생성(db 저장)
            ChatRoomUserEntity roomUser = ChatRoomUserEntity.builder()
                    .chatRoom(saved)
                    .member(creator)
                    .joinedAt(LocalDateTime.now())
                    .build();
            
            chatRoomUserRepository.save(roomUser);
        }
        //채팅방 Dto를 반환
        return ChatRoomDto.builder()
                .chatRoomId(saved.getId())
                .title(saved.getTitle())
                .roomType(saved.getRoomType())
                .creatorId(roomDto.getCreatorId())
                .build();
    }

    //채팅방 입작 로직
    @Override
    public void joinChatRoom(Long roomId, Long memberId) {
        // 이미 참여 중인지 확인(db 조회)
        if (chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId).isPresent()) {
            return;
        }

        //참여중이라면 채팅방과 멤버 정보를 매핑 엔티티에 저장
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));
        
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        //채팅방 유저 엔티티에 저장용 매핑 엔티티 생성
        ChatRoomUserEntity roomUser = ChatRoomUserEntity.builder()
                .chatRoom(chatRoom)
                .member(member)
                .joinedAt(LocalDateTime.now())
                .build();

        //저장
        chatRoomUserRepository.save(roomUser);
    }

    @Override
    public void leaveChatRoom(Long roomId, Long memberId) {
        //참여중이라면 채팅방과 멤버 정보를 매핑 엔티티에 저장
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Not participating in this room"));
        
        //삭제
        chatRoomUserRepository.delete(roomUser);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomDto selectChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .map(entity -> ChatRoomDto.builder()
                        .chatRoomId(entity.getId())
                        .title(entity.getTitle())
                        .roomType(entity.getRoomType())
                        .build())
                .orElse(null);
    }

    //채팅방에 메시지 저장
    @Override
    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
        // 1. 채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Chat Room not found"));

        // 2. 발신자 조회
        MemberEntity sender = memberRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 3. 메시지 엔티티 생성 및 저장
        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .sender(sender) 
                .content(messageDto.getContent())
                .messageType(messageDto.getMessageType())
                .createdAt(LocalDateTime.now())
                .build();
        
        ChatMessageEntity savedMessage = chatMessageRepository.save(messageEntity);
        
        // 4. 저장된 메시지를 DTO로 변환하여 반환(갱신된 정보만 세팅)
        messageDto.setMessageId(savedMessage.getId());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());
        messageDto.setSenderName(sender.getName());
        messageDto.setSenderProfileImage(sender.getProfileImageUrl());
        
        return messageDto;
    }

    //채팅방 메시지 조회
    @Override
    public List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId) {
        // 한 번에 불러올 메시지 개수
        int pageSize = 30;
        Pageable pageable = PageRequest.of(0, pageSize);
        
        Page<ChatMessageEntity> messagePage;

        if (cursorId == null || cursorId == 0) {
            // 커서가 없으면 가장 최신 메시지부터 조회
            messagePage = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);
        } else {
            // 커서(마지막 메시지 ID)보다 작은(이전) 메시지 조회
            messagePage = chatMessageRepository.findByChatRoomIdAndIdLessThan(roomId, cursorId, pageable);
        }

        //Dto 객체로 변환
        return messagePage.getContent().stream()
                .map(entity -> ChatMessageDto.builder()
                        .messageId(entity.getId())
                        .chatRoomId(entity.getChatRoom().getId())
                        .senderId(entity.getSender().getId())
                        .senderName(entity.getSender().getName())
                        .senderProfileImage(entity.getSender().getProfileImageUrl())
                        .content(entity.getContent())
                        .messageType(entity.getMessageType())
                        .createdAt(entity.getCreatedAt())
                        .build())
                // DB에서 최신순(DESC)으로 가져왔으므로, 화면에 뿌릴 때는 다시 과거순(ASC)으로 뒤집어서 출력
                .sorted(Comparator.comparing(ChatMessageDto::getCreatedAt)) 
                .collect(Collectors.toList());
    }
}
