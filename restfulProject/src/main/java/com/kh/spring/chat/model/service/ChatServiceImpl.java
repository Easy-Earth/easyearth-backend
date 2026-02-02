package com.kh.spring.chat.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.repository.ChatMessageRepository;
import com.kh.spring.chat.model.repository.ChatRoomRepository;
import com.kh.spring.chat.model.vo.ChatMessageEntity;
import com.kh.spring.chat.model.vo.ChatRoomEntity;
import com.kh.spring.chat.model.vo.MemberEntity;
import com.kh.spring.chat.model.repository.MemberRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> selectChatRoomList() {
        return chatRoomRepository.findAll().stream()
                .map(entity -> ChatRoomDto.builder()
                        .chatRoomId(entity.getId())
                        .title(entity.getTitle())
                        .roomType(entity.getRoomType())
                        .lastMessageContent(entity.getLastMessageContent())
                        .lastMessageAt(entity.getLastMessageAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ChatRoomDto createChatRoom(ChatRoomDto roomDto) {
        ChatRoomEntity entity = ChatRoomEntity.builder()
                .title(roomDto.getTitle())
                .roomType(roomDto.getRoomType())
                .createdAt(LocalDateTime.now())
                .build();
        
        ChatRoomEntity saved = chatRoomRepository.save(entity);
        
        return ChatRoomDto.builder()
                .chatRoomId(saved.getId())
                .title(saved.getTitle())
                .roomType(saved.getRoomType())
                .build();
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
        
        // 4. 저장된 메시지를 DTO로 변환하여 반환
        messageDto.setMessageId(savedMessage.getId());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());
        messageDto.setSenderName(sender.getName());
        messageDto.setSenderProfileImage(sender.getProfileImageUrl());
        
        return messageDto;
    }

    @Override
    public List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId) {
        // 한 번에 불러올 메시지 개수
        int pageSize = 50;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, pageSize);
        
        org.springframework.data.domain.Page<ChatMessageEntity> messagePage;

        if (cursorId == null || cursorId == 0) {
            // 커서가 없으면 가장 최신 메시지부터 조회
            messagePage = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);
        } else {
            // 커서(마지막 메시지 ID)보다 작은(이전) 메시지 조회
            messagePage = chatMessageRepository.findByChatRoomIdAndIdLessThan(roomId, cursorId, pageable);
        }

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
                // DB에서 최신순(DESC)으로 가져왔으므로, 화면에 뿌릴 때는 다시 과거순(ASC)으로 뒤집는 게 보통 편함
                // (프론트 스타일에 따라 다르지만 보통 위에서 아래로 읽으므로)
                // 여기서는 리스트 순서를 뒤집어서 반환
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())) 
                .collect(Collectors.toList());
    }
}
