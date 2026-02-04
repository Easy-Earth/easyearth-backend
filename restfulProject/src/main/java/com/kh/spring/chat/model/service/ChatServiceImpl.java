package com.kh.spring.chat.model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import com.kh.spring.chat.model.dto.ChatMessageDto;
import com.kh.spring.chat.model.dto.ChatRoomDto;
import com.kh.spring.chat.model.repository.ChatMessageRepository;
import com.kh.spring.chat.model.repository.ChatRoomRepository;
import com.kh.spring.chat.model.repository.ChatRoomUserRepository;
import com.kh.spring.chat.model.repository.MemberRepository;
import com.kh.spring.chat.model.repository.MessageReactionRepository;
import com.kh.spring.chat.model.vo.ChatMessageEntity;
import com.kh.spring.chat.model.vo.ChatRoomEntity;
import com.kh.spring.chat.model.vo.ChatRoomUserEntity;
import com.kh.spring.chat.model.vo.MemberEntity;
import com.kh.spring.chat.model.vo.MessageReactionEntity;

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
                    .role("OWNER") // 방장은 OWNER 권한
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
        
        // [방장 탈퇴 정책] OWNER는 혼자 남은 경우가 아니면 나갈 수 없음 (위임 필수)
        if ("OWNER".equals(roomUser.getRole())) {
            long remainingCount = chatRoomUserRepository.countByChatRoomId(roomId);
            if (remainingCount > 1) {
                throw new IllegalArgumentException("다른 사람에게 방장을 위임하고 나가세요.");
            }
        }
        
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
        ChatMessageEntity.ChatMessageEntityBuilder messageBuilder = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .sender(sender) 
                .content(messageDto.getContent())
                .messageType(messageDto.getMessageType())
                .createdAt(LocalDateTime.now());
        
        // [답장/인용] 부모 메시지 연결
        if (messageDto.getParentMessageId() != null) {
            ChatMessageEntity parent = chatMessageRepository.findById(messageDto.getParentMessageId())
                    .orElse(null);
            if (parent != null) {
                messageBuilder.parentMessage(parent);
            }
        }
        
        ChatMessageEntity messageEntity = messageBuilder.build();
        
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
    public List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId, Long memberId) {
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
                .map(entity -> convertToDto(entity, memberId))
                // DB에서 최신순(DESC)으로 가져왔으므로, 화면에 뿌릴 때는 다시 과거순(ASC)으로 뒤집어서 출력
                .sorted(Comparator.comparing(ChatMessageDto::getCreatedAt)) 
                .collect(Collectors.toList());
    }

    // [메시지 검색] 키워드 검색
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> searchMessages(Long chatRoomId, Long memberId, String keyword) {
        // 1. 참여 여부 확인 (보안)
        if (chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId).isEmpty()) {
            throw new IllegalArgumentException("Not participating in this room");
        }
        
        // 2. 검색 (최신순)
        List<ChatMessageEntity> entities = chatMessageRepository.findByChatRoomIdAndContentContainingOrderByCreatedAtDesc(chatRoomId, keyword);
        
        // 3. DTO 변환 및 반환
        return entities.stream()
                .map(entity -> convertToDto(entity, memberId))
                .collect(Collectors.toList());
    }

    // DTO 변환 헬퍼 메서드
    private ChatMessageDto convertToDto(ChatMessageEntity entity, Long memberId) {
        // 리액션 가공
        List<ChatMessageDto.ReactionSummary> reactionSummaries = entity.getReactions().stream()
            .collect(Collectors.groupingBy(MessageReactionEntity::getEmojiType))
            .entrySet().stream()
            .map(entry -> {
                String emoji = entry.getKey();
                List<MessageReactionEntity> list = entry.getValue();
                
                boolean me = false;
                if (memberId != null) {
                    me = list.stream().anyMatch(r -> r.getMember().getId().equals(memberId));
                }
                
                return ChatMessageDto.ReactionSummary.builder()
                        .emojiType(emoji)
                        .count(list.size())
                        .selectedByMe(me)
                        .build();
            })
            .collect(Collectors.toList());

        ChatMessageDto.ChatMessageDtoBuilder builder = ChatMessageDto.builder()
            .messageId(entity.getId())
            .chatRoomId(entity.getChatRoom().getId())
            .senderId(entity.getSender().getId())
            .senderName(entity.getSender().getName())
            .senderProfileImage(entity.getSender().getProfileImageUrl())
            .content(entity.getContent())
            .messageType(entity.getMessageType())
            .createdAt(entity.getCreatedAt())
            .reactions(reactionSummaries);



        // [답장/인용] 부모 메시지 정보 채우기
        if (entity.getParentMessage() != null) {
            builder.parentMessageId(entity.getParentMessage().getId());
            builder.parentMessageContent(entity.getParentMessage().getContent());
            builder.parentMessageSenderName(entity.getParentMessage().getSender().getName());
        }

        return builder.build();
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

    private final MessageReactionRepository messageReactionRepository;

    // 리액션 토글 로직
    @Override
    public void toggleReaction(Long messageId, Long memberId, String emojiType) {
        // 1. 메시지 존재 확인
        ChatMessageEntity message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // 2. 멤버 존재 확인
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 3. 기존 리액션 확인
        Optional<MessageReactionEntity> existingReactionOpt = 
                messageReactionRepository.findByChatMessageIdAndMemberId(messageId, memberId);

        if (existingReactionOpt.isPresent()) {
            MessageReactionEntity existingReaction = existingReactionOpt.get();
            
            if (existingReaction.getEmojiType().equals(emojiType)) {
                // 3-1. 같은 이모지면 삭제 (토글 Off)
                messageReactionRepository.delete(existingReaction);
            } else {
                // 3-2. 다른 이모지면 업데이트 (변경)
                existingReaction.updateEmoji(emojiType);
            }
        } else {
            // 4. 없으면 신규 생성
            MessageReactionEntity reaction = MessageReactionEntity.builder()
                    .chatMessage(message)
                    .member(member)
                    .emojiType(emojiType)
                    .build();
            
            messageReactionRepository.save(reaction);
    }
    }

    // [그룹 관리] 역할 변경
    @Override
    public void updateRole(Long chatRoomId, Long targetMemberId, Long requesterId, String newRole) {
        // 1. 요청자 권한 확인
        ChatRoomUserEntity requester = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not in room"));
        
        if (!"OWNER".equals(requester.getRole())) {
             throw new IllegalArgumentException("Only OWNER can change roles");
        }
        
        // 2. 대상 조회
        ChatRoomUserEntity target = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not in room"));

        // 3. 로직 처리
        // 만약 방장을 위임하는 경우 (OWNER -> MEMBER, Target -> OWNER)
        if ("OWNER".equals(newRole)) {
            // 기존 방장은 MEMBER로 강등
            requester.setRole("MEMBER");
            target.setRole("OWNER");
            
            // 시스템 메시지
            saveSystemMessage(requester.getChatRoom(), requester.getMember().getName() + "님이 방장을 위임했습니다.");
        } else {
            // 단순 관리자(ADMIN) 등 변경
            target.setRole(newRole);
        }
    }

    // [그룹 관리] 멤버 강퇴
    @Override
    public void kickMember(Long chatRoomId, Long targetMemberId, Long requesterId) {
        // 1. 요청자 조회
        ChatRoomUserEntity requester = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not in room"));
        
        // 2. 권한 확인 (OWNER나 ADMIN만 강퇴 가능)
        if (!"OWNER".equals(requester.getRole()) && !"ADMIN".equals(requester.getRole())) {
            throw new IllegalArgumentException("No permission to kick");
        }
        
        // 3. 대상 조회
        ChatRoomUserEntity target = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not in room"));
        
        // (옵션) 방장은 강퇴 불가
        if ("OWNER".equals(target.getRole())) {
            throw new IllegalArgumentException("Cannot kick the OWNER");
        }

        // 4. 강퇴 (삭제)
        String targetName = target.getMember().getName();
        chatRoomUserRepository.delete(target);
        
        // 5. 시스템 메시지
        saveSystemMessage(requester.getChatRoom(), targetName + "님이 강퇴당했습니다.");
    }
}
