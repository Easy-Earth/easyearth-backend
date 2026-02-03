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

        //채팅방들을 리스트로 반환
        return chatRooms.stream()
                .map(entity -> {
                    int unreadCount = 0;
                    if (memberId != null) {
                        // 안 읽은 메시지 수 계산 (최적화: 전체 수 - 내가 읽은 시점 수)
                        unreadCount = countUnreadMessagesOptimized(entity, memberId);
                    }
                    
                    //채팅방 정보를 dto로 변환 (아래 조건문에서 완성 후 build)
                    ChatRoomDto.ChatRoomDtoBuilder builder = ChatRoomDto.builder()
                            .chatRoomId(entity.getId())
                            .title(entity.getTitle())
                            .roomType(entity.getRoomType())
                            .lastMessageContent(entity.getLastMessageContent())
                            .lastMessageAt(entity.getLastMessageAt())
                            .unreadCount(unreadCount);

                    // 1:1 채팅방(SINGLE)이고 상대방 정보를 채워야 하는 경우
                    if ("SINGLE".equals(entity.getRoomType()) && memberId != null) {
                        chatRoomUserRepository.findFirstByChatRoomIdAndMemberIdNot(entity.getId(), memberId)
                            .ifPresent(otherUser -> {
                                builder.otherMemberName(otherUser.getMember().getName());
                                builder.otherMemberProfile(otherUser.getMember().getProfileImageUrl());
                                // 1:1 방의 제목이 비어있으면 상대방 이름으로 설정
                                if (entity.getTitle() == null || entity.getTitle().isEmpty()) {
                                    builder.title(otherUser.getMember().getName());
                                }
                            });
                    }
                    
                    //채팅방 정보를 dto로 변환
                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    // 안 읽은 메시지 갯수 (최적화 로직)
    private int countUnreadMessagesOptimized(ChatRoomEntity chatRoom, Long memberId) {
        // 1. 참여자 정보 조회
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), memberId)
                .orElse(null);

        if (roomUser == null) {
            return 0;
        }

        // 2. 안 읽은 수 = 방 전체 메시지 수 - 내가 마지막으로 읽었을 때의 수
        // (음수가 나오지 않도록 처리, 혹시 모를 동기화 이슈 방지)
        long total = chatRoom.getTotalMessageCount() != null ? chatRoom.getTotalMessageCount() : 0L;
        long read = roomUser.getLastReadMessageCount() != null ? roomUser.getLastReadMessageCount() : 0L;
        
        return (int) Math.max(0, total - read);
    }
    
    //채팅방 생성 로직
    @Override
    public ChatRoomDto createChatRoom(ChatRoomDto roomDto) {
        // 1:1 채팅방이고, 생성자와 맴버가 같으면
        if ("SINGLE".equals(roomDto.getRoomType()) && roomDto.getCreatorId() != null && roomDto.getTargetMemberId() != null) {
             // 두 사용자 간의 1:1 방이 존재하는지 확인
             ChatRoomEntity existingRoom = chatRoomRepository.findExistingSingleRoom(roomDto.getCreatorId(), roomDto.getTargetMemberId())
                     .orElse(null);
             
             if (existingRoom != null) {
                 return ChatRoomDto.builder()
                         .chatRoomId(existingRoom.getId())
                         .title(existingRoom.getTitle())
                         .roomType(existingRoom.getRoomType())
                         .build();
             }
        }
        
        // 없다면 기존 로직대로 새로 생성
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

        // 3. 1:1 채팅인 경우 타겟 유저도 바로 참여 처리 (KakaoTalk Style)
        if ("SINGLE".equals(roomDto.getRoomType()) && roomDto.getTargetMemberId() != null) {
             MemberEntity target = memberRepository.findById(roomDto.getTargetMemberId())
                     .orElseThrow(() -> new IllegalArgumentException("Target Member not found"));

             ChatRoomUserEntity targetUser = ChatRoomUserEntity.builder()
                     .chatRoom(saved)
                     .member(target)
                     .joinedAt(LocalDateTime.now())
                     .build();
             
             chatRoomUserRepository.save(targetUser);
        }
        
        //채팅방 Dto를 반환
        return ChatRoomDto.builder()
                .chatRoomId(saved.getId())
                .title(saved.getTitle())
                .roomType(saved.getRoomType())
                .creatorId(roomDto.getCreatorId())
                .build();
    }

    //채팅방 입장 로직
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
        
        // [System Message] 입장 메시지 생성
        saveSystemMessage(chatRoom, member.getName() + "님이 들어왔습니다.");
    }

    //채팅방 나가기
    @Override
    public void leaveChatRoom(Long roomId, Long memberId) {
        //참여중이라면 채팅방과 멤버 정보를 매핑 엔티티에 저장
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Not participating in this room"));
        
        //퇴장 메시지 전송을 위해 멤버 이름 조회
        String memberName = roomUser.getMember().getName();
        ChatRoomEntity chatRoom = roomUser.getChatRoom();

        //삭제
        chatRoomUserRepository.delete(roomUser);
        
        // [System Message] 퇴장 메시지 생성
        saveSystemMessage(chatRoom, memberName + "님이 나갔습니다.");
    }
    
    //채팅방 상세 조회
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
        
        // 4. 채팅방의 마지막 메시지 정보 업데이트
        chatRoom.updateLastMessage(messageDto.getContent(), savedMessage.getCreatedAt());
        // (Dirty checking으로 인해 transaction 종료 시 update query 실행됨)
        
        // 5. 저장된 메시지를 DTO로 변환하여 반환
        messageDto.setMessageId(savedMessage.getId());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());
        messageDto.setSenderName(sender.getName());
        messageDto.setSenderProfileImage(sender.getProfileImageUrl());
        
        return messageDto;
    }
    
    private static final int PAGE_SIZE = 30;

    //채팅방 메시지 조회
    @Override
    public List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        
        Slice<ChatMessageEntity> messageSlice;

        if (cursorId == null || cursorId == 0) {
            // 커서가 없으면 가장 최신 메시지부터 조회
            messageSlice = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);
        } else {
            // 커서(마지막 메시지 ID)보다 작은(이전) 메시지 조회
            messageSlice = chatMessageRepository.findByChatRoomIdAndIdLessThan(roomId, cursorId, pageable);
        }

        //Dto 객체로 변환
        return messageSlice.getContent().stream()
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

    //메시지 읽음 처리 (Optimized)
    @Override
    public void updateReadStatus(Long roomId, Long memberId, Long lastMessageId) {
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("User is not in the room"));
        
        // 현재 방의 총 메시지 수를 가져와서 업데이트
        // (채팅방에 들어오면 "현재까지 온 모든 메시지"를 읽은 것으로 간주)
        ChatRoomEntity chatRoom = roomUser.getChatRoom();
        Long currentTotalCount = chatRoom.getTotalMessageCount();
        if (currentTotalCount == null) {
            currentTotalCount = 0L;
        }

        // ID 기반 읽음 처리 업데이트
        roomUser.updateLastReadMessageId(lastMessageId);
        // 카운트 기반 읽음 처리 업데이트
        roomUser.updateLastReadMessageCount(currentTotalCount);
    }

    // 시스템 메시지 저장
    private void saveSystemMessage(ChatRoomEntity chatRoom, String content) {
        // 시스템 메시지는 발신자가 없으므로(null) '시스템' 더미 계정을 만들어서 사용
        // Admin 계정을 찾아서 넣는 로직으로 구현.(id가 1인 멤버가 admin)
        MemberEntity systemSender = memberRepository.findById(1L)
                 .orElse(null); 
        
        if (systemSender == null) {
            // Admin 계정이 없으면 시스템 메시지 실패 로그만 남기고 리턴 (전체 로직 에러 방지)
            log.warn("System Account (ID=1) not found. System message skipped: {}", content);
            return;
        }

        ChatMessageEntity systemMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .sender(systemSender)
                .content(content)
                .messageType("SYSTEM") // ENTER, LEAVE 등 구체적으로도 가능
                .createdAt(LocalDateTime.now())
                .build();
        
        chatMessageRepository.save(systemMessage);
    }
}
