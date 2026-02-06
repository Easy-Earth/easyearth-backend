package com.kh.spring.chat.model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import jakarta.persistence.OptimisticLockException;
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
    private final MessageReactionRepository messageReactionRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

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
            throw new IllegalArgumentException("회원을 찾을 수 없습니다");
        }

        //채팅방들을 리스트로 반환
        return chatRooms.stream()
                .map(entity -> {
                    int unreadCount = 0;
                    boolean isFavorite = false;
                    String invitationStatus = "ACCEPTED";
                    
                    if (memberId != null) {
                        // 안 읽은 메시지 수 계산 (최적화: 전체 수 - 내가 읽은 시점 수)
                        unreadCount = countUnreadMessagesOptimized(entity, memberId);
                        
                        // 즐겨찾기 및 초대 상태 조회
                        chatRoomUserRepository.findByChatRoomIdAndMemberId(entity.getId(), memberId)
                            .ifPresent(roomUser -> {
                                // isFavorite가 1이면 true, 0이면 false
                            });
                    }
                    
                    // ChatRoomUserEntity에서 즐겨찾기, 초대 상태 조회
                    ChatRoomUserEntity userInfo = null;
                    if (memberId != null) {
                        userInfo = chatRoomUserRepository
                            .findByChatRoomIdAndMemberId(entity.getId(), memberId)
                            .orElse(null);
                        
                        if (userInfo != null) {
                            isFavorite = (userInfo.getIsFavorite() == 1);
                            invitationStatus = userInfo.getInvitationStatus();
                        }
                    }
                    
                    //채팅방 정보를 dto로 변환 (아래 조건문에서 완성 후 build)
                    ChatRoomDto.ChatRoomDtoBuilder builder = ChatRoomDto.builder()
                            .chatRoomId(entity.getId())
                            .title(entity.getTitle())
                            .roomType(entity.getRoomType())
                            .lastMessageContent(entity.getLastMessageContent())
                            .lastMessageAt(entity.getLastMessageAt())
                            .unreadCount(unreadCount)
                            .isFavorite(isFavorite)
                            .invitationStatus(invitationStatus);

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
                    .orElseThrow(() -> new IllegalArgumentException("생성자를 찾을 수 없습니다"));

            // [Role 정책] 1:1 채팅은 MEMBER, GROUP 채팅은 생성자가 OWNER
            String creatorRole = "SINGLE".equals(roomDto.getRoomType()) ? "MEMBER" : "OWNER";
            
            //매핑 엔티티 생성(db 저장)
            ChatRoomUserEntity roomUser = ChatRoomUserEntity.builder()
                    .chatRoom(saved)
                    .member(creator)
                    .role(creatorRole)
                    .joinedAt(LocalDateTime.now())
                    .build();
            
            chatRoomUserRepository.save(roomUser);
        }

        // 3. 1:1 채팅인 경우 타겟 유저도 바로 참여 처리 (KakaoTalk Style)
        if ("SINGLE".equals(roomDto.getRoomType()) && roomDto.getTargetMemberId() != null) {
             MemberEntity target = memberRepository.findById(roomDto.getTargetMemberId())
                     .orElseThrow(() -> new IllegalArgumentException("대상 회원을 찾을 수 없습니다"));

             // [Role 정책] 1:1 채팅은 둘 다 MEMBER (대등한 관계)
             ChatRoomUserEntity targetUser = ChatRoomUserEntity.builder()
                     .chatRoom(saved)
                     .member(target)
                     .role("MEMBER")
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
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다"));
        
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

        //채팅방 유저 엔티티에 저장용 매핑 엔티티 생성
        ChatRoomUserEntity roomUser = ChatRoomUserEntity.builder()
                .chatRoom(chatRoom)
                .member(member)
                .role("MEMBER")  // [명시적 설정] @Builder.Default에만 의존하지 않음
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
        // [동시성 제어] 비관적 락으로 조회 (OWNER 퇴장 시 Race Condition 방지)
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberIdWithLock(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하고 있지 않습니다"));
        
        ChatRoomEntity chatRoom = roomUser.getChatRoom();
        
        // [방장 탈퇴 정책] GROUP 채팅방에서만 OWNER는 혼자 남은 경우가 아니면 나갈 수 없음 (위임 필수)
        // 1:1 채팅(SINGLE)은 자유롭게 나갈 수 있음
        if ("GROUP".equals(chatRoom.getRoomType()) && "OWNER".equals(roomUser.getRole())) {
            long remainingCount = chatRoomUserRepository.countByChatRoomId(roomId);
            if (remainingCount > 1) {
                throw new IllegalArgumentException("다른 사람에게 방장을 위임하고 나가세요.");
            }
        }
        
        //퇴장 메시지 전송을 위해 멤버 이름 조회
        String memberName = roomUser.getMember().getName();

        //삭제
        chatRoomUserRepository.delete(roomUser);
        
        // [System Message] 퇴장 메시지 생성
        saveSystemMessage(chatRoom, memberName + "님이 나갔습니다.");
        
        // [정합성] 마지막 멤버가 나가면 빈 채팅방 삭제
        long remainingMembers = chatRoomUserRepository.countByChatRoomId(roomId);
        if (remainingMembers == 0) {
            log.info("마지막 멤버 퇴장으로 채팅방 삭제: roomId={}", roomId);
            chatRoomRepository.deleteById(roomId);
        }
    }
    
    //채팅방 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ChatRoomDto selectChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .map(entity -> {
                    // 참여자 목록 조회 및 DTO 변환
                    List<ChatRoomDto.ParticipantInfo> participants = chatRoomUserRepository
                            .findAllByChatRoomId(roomId).stream()
                            .map(roomUser -> ChatRoomDto.ParticipantInfo.builder()
                                    .memberId(roomUser.getMember().getId())
                                    .memberName(roomUser.getMember().getName())
                                    .name(roomUser.getMember().getName()) // 프론트 호환성
                                    .loginId(roomUser.getMember().getLoginId())
                                    .profileImageUrl(roomUser.getMember().getProfileImageUrl())
                                    .role(roomUser.getRole())
                                    .joinedAt(roomUser.getJoinedAt())
                                    .build())
                            .collect(Collectors.toList());
                    
                    return ChatRoomDto.builder()
                            .chatRoomId(entity.getId())
                            .title(entity.getTitle())
                            .roomName(entity.getTitle())  // 프론트엔드 호환성
                            .roomType(entity.getRoomType())
                            .participants(participants)
                            .noticeContent(entity.getNoticeContent())  // 공지 내용 추가
                            .noticeMessageId(entity.getNoticeMessageId())  // 공지 메시지 ID 추가
                            .build();
                })
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다"));
    }
    
    //채팅방에 메시지 저장
    @Override
    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
        // 1. 채팅방 조회
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다"));

        // [보안] 발신자가 채팅방 멤버인지 확인
        if (chatRoomUserRepository.findByChatRoomIdAndMemberId(
                messageDto.getChatRoomId(), messageDto.getSenderId()).isEmpty()) {
            throw new IllegalArgumentException("채팅방 멤버만 메시지를 보낼 수 있습니다");
        }

        // 2. 발신자 조회
        MemberEntity sender = memberRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

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
                // [보안] 부모 메시지가 같은 채팅방에 속해 있는지 확인
                if (!parent.getChatRoom().getId().equals(messageDto.getChatRoomId())) {
                    throw new IllegalArgumentException("부모 메시지는 같은 채팅방에 있어야 합니다");
                }
                messageBuilder.parentMessage(parent);
            }
        }
        
        ChatMessageEntity messageEntity = messageBuilder.build();
        
        ChatMessageEntity savedMessage = chatMessageRepository.save(messageEntity);
        
        // 4. 채팅방의 마지막 메시지 정보 업데이트 (OptimisticLock 재시도)
        updateLastMessageWithRetry(chatRoom.getId(), messageDto.getContent(), savedMessage.getCreatedAt());
        
        // 5. [개선] 발신자 자동 읽음 처리
        try {
            updateReadStatus(chatRoom.getId(), sender.getId(), savedMessage.getId());
        } catch (Exception e) {
            log.warn("발신자 자동 읽음 처리 실패: {}", e.getMessage());
            // 읽음 처리 실패는 메시지 전송에 영향을 주지 않도록 warn 로그만 남김
        }
        
        // 6. 저장된 메시지를 DTO로 변환하여 반환
        messageDto.setMessageId(savedMessage.getId());
        messageDto.setCreatedAt(savedMessage.getCreatedAt());
        messageDto.setSenderName(sender.getName());
        messageDto.setSenderProfileImage(sender.getProfileImageUrl());
        
        // [수정] 방금 보낸 메시지의 안 읽은 사람 수 계산 (발신자 제외 모든 사람)
        Integer unreadCount = calculateUnreadCount(savedMessage);
        messageDto.setUnreadCount(unreadCount);
        log.info("💬 메시지 저장 완료 - messageId: {}, unreadCount: {}, content: {}", 
            savedMessage.getId(), unreadCount, messageDto.getContent());
        
        return messageDto;
    }
    
    /**
     * [동시성 제어] OptimisticLockException 발생 시 재시도하는 updateLastMessage
     */
    private void updateLastMessageWithRetry(Long roomId, String content, LocalDateTime createdAt) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // 최신 엔티티 다시 조회 (버전 정보 포함)
                ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                        .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다"));
                
                chatRoom.updateLastMessage(content, createdAt);
                chatRoomRepository.save(chatRoom); // 명시적 save로 즉시 flush
                
                return; // 성공 시 종료
                
            } catch (OptimisticLockException e) {
                retryCount++;
                log.warn("OptimisticLockException 발생 (재시도 {}/{}): roomId={}", retryCount, maxRetries, roomId);
                
                if (retryCount >= maxRetries) {
                    log.error("재시도 한도 초과. 마지막 메시지 업데이트 실패: roomId={}", roomId);
                    // 메시지는 저장되었으므로 예외를 던지지 않고 로그만 남김
                    return;
                }
                
                // 짧은 대기 후 재시도 (exponential backoff)
                try {
                    Thread.sleep(50 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
    
    private static final int PAGE_SIZE = 30;

    //채팅방 메시지 조회
    @Override
    public List<ChatMessageDto> selectMessageList(Long roomId, Long cursorId, Long memberId) {
        // [보안] 참여자만 메시지 조회 가능
        if (memberId != null && chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId).isEmpty()) {
            throw new IllegalArgumentException("채팅방 멤버만 조회 가능합니다");
        }
        
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
            throw new IllegalArgumentException("채팅방에 참여하고 있지 않습니다");
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

        // [개선] 읽음 수 계산
        Integer unreadCount = calculateUnreadCount(entity);

        ChatMessageDto.ChatMessageDtoBuilder builder = ChatMessageDto.builder()
            .messageId(entity.getId())
            .chatRoomId(entity.getChatRoom().getId())
            .senderId(entity.getSender().getId())
            .senderName(entity.getSender().getName())
            .senderProfileImage(entity.getSender().getProfileImageUrl())
            .content(entity.getContent())
            .messageType(entity.getMessageType())
            .createdAt(entity.getCreatedAt())
            .reactions(reactionSummaries)
            .unreadCount(unreadCount);



        // [답장/인용] 부모 메시지 정보 채우기
        if (entity.getParentMessage() != null) {
            builder.parentMessageId(entity.getParentMessage().getId());
            builder.parentMessageContent(entity.getParentMessage().getContent());
            builder.parentMessageSenderName(entity.getParentMessage().getSender().getName());
        }

        return builder.build();
    }
    
    // [개선] 메시지별 안 읽은 사람 수 계산
    private Integer calculateUnreadCount(ChatMessageEntity message) {
        try {
            Long chatRoomId = message.getChatRoom().getId();
            Long messageId = message.getId();
            Long senderId = message.getSender().getId();
            
            // 채팅방 전체 참여자 조회
            List<ChatRoomUserEntity> allUsers = chatRoomUserRepository
                .findAllByChatRoomId(chatRoomId);
            
            // 발신자 제외한 참여자 수
            long totalRecipients = allUsers.stream()
                .filter(u -> !u.getMember().getId().equals(senderId))
                .count();
            
            // 읽은 사람 수 계산 (lastReadMessageId >= 현재 메시지 ID)
            long readCount = allUsers.stream()
                .filter(u -> !u.getMember().getId().equals(senderId))
                .filter(u -> {
                    Long lastReadId = u.getLastReadMessageId();
                    return lastReadId != null && lastReadId >= messageId;
                })
                .count();
            
            // 안 읽은 사람 수
            int unread = (int) (totalRecipients - readCount);
            return unread > 0 ? unread : null; // 0이면 null 반환 (프론트엔드에서 표시 안 함)
        } catch (Exception e) {
            log.warn("읽음 수 계산 실패: {}", e.getMessage());
            return null; // 계산 실패 시 null 반환
        }
    }

    //메시지 읽음 처리 (Optimized)
    @Override
    public void updateReadStatus(Long roomId, Long memberId, Long lastMessageId) {
        ChatRoomUserEntity roomUser = chatRoomUserRepository.findByChatRoomIdAndMemberId(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 채팅방에 없습니다"));
        
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
        
        // ✨ 읽음 처리 후 영향받는 메시지들의 unreadCount 재계산
        List<ChatMessageEntity> affectedMessages = chatMessageRepository.findByChatRoomIdAndIdLessThanEqual(
                roomId, lastMessageId);
        
        Map<Long, Integer> unreadCountMap = new HashMap<>();
        for (ChatMessageEntity message : affectedMessages) {
            Integer count = calculateUnreadCount(message);
            unreadCountMap.put(message.getId(), count);
        }
        
        // ✨ 실시간 갱신 이벤트 전송 (업데이트된 unreadCount 포함)
        Map<String, Object> readEvent = new HashMap<>();
        readEvent.put("type", "READ_UPDATE");
        readEvent.put("memberId", memberId);
        readEvent.put("lastMessageId", lastMessageId);
        readEvent.put("unreadCountMap", unreadCountMap);  // 메시지별 업데이트된 unreadCount
        
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId + "/read", readEvent);
        log.debug("읽음 상태 실시간 이벤트 전송: roomId={}, memberId={}, lastMessageId={}, affectedMessages={}", 
                roomId, memberId, lastMessageId, affectedMessages.size());
    }

    // 시스템 메시지 저장
    private void saveSystemMessage(ChatRoomEntity chatRoom, String content) {
        log.info("🟢 [시스템 메시지] 저장 시작 - chatRoomId: {}, content: {}", chatRoom.getId(), content);
        
        // 시스템 메시지는 발신자가 없으므로(null) '시스템' 더미 계정을 만들어서 사용
        // Admin 계정을 찾아서 넣는 로직으로 구현.(id가 1인 멤버가 admin)
        MemberEntity systemSender = memberRepository.findById(1L)
                 .orElse(null); 
        
        if (systemSender == null) {
            // [개선] Admin 계정이 없으면 시스템 메시지를 건너뜀 (에러 발생 방지)
            log.warn("❌ System Account (ID=1) not found. System message skipped: {}", content);
            return;
        }
        
        log.info("✅ System Account 찾음: {}", systemSender.getName());

        // [예외 처리] 시스템 메시지 저장 실패 시에도 전체 트랜잭션은 유지
        try {
            ChatMessageEntity systemMessage = ChatMessageEntity.builder()
                    .chatRoom(chatRoom)
                    .sender(systemSender)
                    .content(content)
                    .messageType("SYSTEM") // ENTER, LEAVE 등 구체적으로도 가능
                    .createdAt(LocalDateTime.now())
                    .build();
            
            ChatMessageEntity saved = chatMessageRepository.save(systemMessage);
            log.info("💾 DB 저장 완료 - messageId: {}", saved.getId());
            
            // ✅ WebSocket으로 실시간 전송 추가
            ChatMessageDto messageDto = ChatMessageDto.builder()
                    .messageId(saved.getId())
                    .chatRoomId(chatRoom.getId())
                    .senderId(systemSender.getId())
                    .senderName("시스템")
                    .content(content)
                    .messageType("SYSTEM")
                    .createdAt(saved.getCreatedAt())
                    .build();
            
            String topic = "/topic/chat/room/" + chatRoom.getId();
            log.info("📡 WebSocket 전송 - topic: {}, messageDto: {}", topic, messageDto);
            
            messagingTemplate.convertAndSend(topic, messageDto);
            
            log.info("✅ 시스템 메시지 저장 및 전송 완료!");
            
        } catch (Exception e) {
            log.error("❌ Failed to save system message: {}", content, e);
        }
    }
    
    /**
     * [알림] 글로벌 알림 전송 (비동기, @Transactional(readOnly = true))
     */
    @Override
    @Transactional(readOnly = true)
    public void sendGlobalNotifications(ChatMessageDto savedMessage) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<ChatRoomUserEntity> users = chatRoomUserRepository.findAllByChatRoomId(savedMessage.getChatRoomId());
                
                for (ChatRoomUserEntity user : users) {
                    // 본인에게는 알림 보낼 필요 없음
                    if (user.getMember().getId().equals(savedMessage.getSenderId())) {
                        continue;
                    }
                    
                    com.kh.spring.chat.model.dto.ChatNotificationDto notification = com.kh.spring.chat.model.dto.ChatNotificationDto.builder()
                        .targetMemberId(user.getMember().getId())
                        .type("CHAT")
                        .chatRoomId(savedMessage.getChatRoomId())
                        .senderName(savedMessage.getSenderName())
                        .content(savedMessage.getContent())
                        .createdAt(LocalDateTime.now())
                        .url("/chat/room/" + savedMessage.getChatRoomId())
                        .build();
                    
                    messagingTemplate.convertAndSend("/topic/user/" + user.getMember().getId(), notification);
                }
            } catch (Exception e) {
                log.error("Global notification failed", e);
            }
        });
    }

    // 리액션 토글 로직
    @Override
    public void toggleReaction(Long messageId, Long memberId, String emojiType) {
        // 1. 메시지 존재 확인
        ChatMessageEntity message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));

        // [보안] 메시지가 속한 채팅방의 멤버인지 확인
        Long chatRoomId = message.getChatRoom().getId();
        if (chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId).isEmpty()) {
            throw new IllegalArgumentException("채팅방 멤버만 리액션을 남길 수 있습니다");
        }

        // 2. 멤버 존재 확인
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));

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
        
        // ✨ 실시간 갱신: 업데이트된 메시지 전체 전송
        ChatMessageDto updatedMsg = convertToDto(message, memberId);
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatRoomId + "/reaction", updatedMsg);
        log.debug("공감 실시간 이벤트 전송: roomId={}, messageId={}, memberId={}", chatRoomId, messageId, memberId);
    }

    // [그룹 관리] 역할 변경
    @Override
    public void updateRole(Long chatRoomId, Long targetMemberId, Long requesterId, String newRole) {
        log.info("🔄 [역할 변경 요청] chatRoomId: {}, targetMemberId: {}, newRole: {}, requesterId: {}", 
                chatRoomId, targetMemberId, newRole, requesterId);
        
        // [보안] 자기 자신에게 권한 변경 불가
        if (targetMemberId.equals(requesterId)) {
            throw new IllegalArgumentException("자기 자신의 권한은 변경할 수 없습니다");
        }
        
        // 1. 요청자 권한 확인
        ChatRoomUserEntity requester = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자가 채팅방에 없습니다"));
        
        // [정책] 1:1 채팅방에서는 역할 변경 불가
        if ("SINGLE".equals(requester.getChatRoom().getRoomType())) {
            throw new IllegalArgumentException("1:1 채팅방에서는 역할 변경이 불가능합니다");
        }
        
        if (!"OWNER".equals(requester.getRole())) {
             throw new IllegalArgumentException("방장만 권한을 변경할 수 있습니다");
        }
        
        // 2. 대상 조회
        ChatRoomUserEntity target = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자가 채팅방에 없습니다"));

        // 3. 로직 처리
        log.info("🎯 [역할 변경 분기] newRole: {}", newRole);
        
        // 만약 방장을 위임하는 경우 (OWNER -> MEMBER, Target -> OWNER)
        if ("OWNER".equals(newRole)) {
            log.info("👑 방장 위임 처리 시작");
            // 기존 방장은 MEMBER로 강등
            requester.setRole("MEMBER");
            target.setRole("OWNER");
            
            // 시스템 메시지
            log.info("📢 시스템 메시지 호출: 방장 위임");
            saveSystemMessage(requester.getChatRoom(), requester.getMember().getName() + "님이 방장을 위임했습니다.");
        } else if ("ADMIN".equals(newRole)) {
            log.info("⬆️ 관리자 승격 처리 시작");
            // 관리자로 승격
            target.setRole("ADMIN");
            log.info("📢 시스템 메시지 호출: 관리자 승격");
            saveSystemMessage(requester.getChatRoom(), target.getMember().getName() + "님이 관리자로 승격되었습니다.");
        } else if ("MEMBER".equals(newRole)) {
            log.info("⬇️ 일반 멤버 강등 처리 시작");
            // 일반 멤버로 강등
            target.setRole("MEMBER");
            log.info("📢 시스템 메시지 호출: 일반 멤버 강등");
            saveSystemMessage(requester.getChatRoom(), target.getMember().getName() + "님이 일반 멤버로 강등되었습니다.");
        } else {
            throw new IllegalArgumentException("유효하지 않은 역할입니다: " + newRole);
        }
        
        log.info("✅ 역할 변경 완료");
    }

    // [그룹 관리] 멤버 강퇴
    @Override
    public void kickMember(Long chatRoomId, Long targetMemberId, Long requesterId) {
        // [보안] 자기 자신을 강퇴할 수 없음
        if (targetMemberId.equals(requesterId)) {
            throw new IllegalArgumentException("자기 자신을 강퇴할 수 없습니다");
        }
        
        // 1. 요청자 조회
        ChatRoomUserEntity requester = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, requesterId)
                .orElseThrow(() -> new IllegalArgumentException("요청자가 채팅방에 없습니다"));
        
        // [정책] 1:1 채팅방에서는 강퇴 불가
        if ("SINGLE".equals(requester.getChatRoom().getRoomType())) {
            throw new IllegalArgumentException("1:1 채팅방에서는 강퇴가 불가능합니다");
        }
        
        // 2. 권한 확인 (OWNER나 ADMIN만 강퇴 가능)
        if (!"OWNER".equals(requester.getRole()) && !"ADMIN".equals(requester.getRole())) {
            throw new IllegalArgumentException("강퇴 권한이 없습니다");
        }
        
        // 3. 대상 조회
        ChatRoomUserEntity target = chatRoomUserRepository.findByChatRoomIdAndMemberId(chatRoomId, targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자가 채팅방에 없습니다"));
        
        // (옵션) 방장은 강퇴 불가
        if ("OWNER".equals(target.getRole())) {
            throw new IllegalArgumentException("방장을 강퇴할 수 없습니다");
        }

        // 4. 강퇴 (삭제)
        String targetName = target.getMember().getName();
        chatRoomUserRepository.delete(target);
        
        // 5. 시스템 메시지
        saveSystemMessage(requester.getChatRoom(), targetName + "님이 강퇴당했습니다.");
        
        // 6. [정합성] 강퇴 후 방이 비었으면 삭제 (leaveChatRoom과 일관성 유지)
        long remainingMembers = chatRoomUserRepository.countByChatRoomId(chatRoomId);
        if (remainingMembers == 0) {
            log.info("강퇴 후 빈 채팅방 삭제: roomId={}", chatRoomId);
            chatRoomRepository.deleteById(chatRoomId);
        }
    }
    
    // ===================================
    // 메시지 삭제 (Soft Delete)
    // ===================================
    
    @Override
    public void softDeleteMessage(Long messageId, Long memberId) {
        // 1. 메시지 조회
        ChatMessageEntity message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));
        
        // 2. [보안] 작성자 본인 확인
        if (!message.getSender().getId().equals(memberId)) {
            throw new IllegalArgumentException("자신의 메시지만 삭제할 수 있습니다");
        }
        
        // 3. Soft Delete: content 및 messageType 변경
        message.setContent("삭제된 메시지입니다");
        message.setMessageType("DELETED");
        chatMessageRepository.save(message);
        
        // 4. WebSocket으로 실시간 전파
        ChatMessageDto dto = convertToDto(message, memberId);
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getChatRoom().getId(), dto);
        
        log.info("메시지 삭제 완료: messageId={}, memberId={}", messageId, memberId);
    }
    
    // ===================================
    // 채팅방 공지 관리
    // ===================================
    
    @Override
    public void setNotice(Long roomId, Long memberId, Long messageId) {
        // 1. 권한 확인 (방장 또는 관리자)
        ChatRoomUserEntity roomUser = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하고 있지 않습니다"));
        
        if (!"OWNER".equals(roomUser.getRole()) && !"ADMIN".equals(roomUser.getRole())) {
            throw new IllegalArgumentException("공지 설정 권한이 없습니다 (방장 또는 관리자만 가능)");
        }
        
        // 2. 메시지 조회
        ChatMessageEntity message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));
        
        // 3. [보안] 메시지가 해당 채팅방에 속해 있는지 확인
        if (!message.getChatRoom().getId().equals(roomId)) {
            throw new IllegalArgumentException("다른 채팅방의 메시지를 공지로 설정할 수 없습니다");
        }
        
        // 4. 공지 설정
        ChatRoomEntity room = roomUser.getChatRoom();
        room.setNoticeContent(message.getContent());
        room.setNoticeMessageId(messageId);
        chatRoomRepository.save(room);
        
        // 5. WebSocket으로 공지 변경 이벤트 전송
        Map<String, Object> noticeEvent = new HashMap<>();
        noticeEvent.put("type", "NOTICE_UPDATED");
        noticeEvent.put("noticeContent", message.getContent());
        noticeEvent.put("noticeMessageId", messageId);
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId + "/notice", noticeEvent);
        
        log.info("공지 설정: roomId={}, messageId={}, memberId={}", roomId, messageId, memberId);
    }
    
    @Override
    public void clearNotice(Long roomId, Long memberId) {
        // 1. 권한 확인
        ChatRoomUserEntity roomUser = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하고 있지 않습니다"));
        
        if (!"OWNER".equals(roomUser.getRole()) && !"ADMIN".equals(roomUser.getRole())) {
            throw new IllegalArgumentException("공지 해제 권한이 없습니다");
        }
        
        // 2. 공지 해제
        ChatRoomEntity room = roomUser.getChatRoom();
        room.setNoticeContent(null);
        room.setNoticeMessageId(null);
        chatRoomRepository.save(room);
        
        // 3. WebSocket 이벤트 전송
        Map<String, Object> noticeEvent = new HashMap<>();
        noticeEvent.put("type", "NOTICE_CLEARED");
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId + "/notice", noticeEvent);
        
        log.info("공지 해제: roomId={}, memberId={}", roomId, memberId);
    }
    
    // [즐겨찾기] 채팅방 즐겨찾기 토글
    @Override
    public void toggleFavorite(Long roomId, Long memberId) {
        log.info("⭐ [즐겨찾기 토글] roomId: {}, memberId: {}", roomId, memberId);
        
        // 1. 참여자 정보 조회
        ChatRoomUserEntity roomUser = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하고 있지 않습니다"));
        
        // 2. 즐겨찾기 토글
        roomUser.toggleFavorite();
        chatRoomUserRepository.save(roomUser);
        
        log.info("✅ 즐겨찾기 변경 완료: isFavorite={}", roomUser.getIsFavorite());
    }
    
    // [초대] 사용자 초대 (PENDING 상태로 추가)
    @Override
    public void inviteUser(Long roomId, Long invitedMemberId, Long requesterId) {
        log.info("📧 [사용자 초대] roomId: {}, invitedMemberId: {}, requesterId: {}", 
                roomId, invitedMemberId, requesterId);
        
        // 1. 초대자 권한 확인
        ChatRoomUserEntity requester = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, requesterId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하고 있지 않습니다"));
        
        // 2. 그룹 채팅방인지 확인
        if ("SINGLE".equals(requester.getChatRoom().getRoomType())) {
            throw new IllegalArgumentException("1:1 채팅방에는 초대할 수 없습니다");
        }
        
        // 3. 이미 참여 중인지 확인
        Optional<ChatRoomUserEntity> existing = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, invitedMemberId);
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 채팅방에 참여 중인 사용자입니다");
        }
        
        // 4. 초대받을 사용자 조회
        MemberEntity invitedMember = memberRepository.findById(invitedMemberId)
            .orElseThrow(() -> new IllegalArgumentException("초대할 사용자를 찾을 수 없습니다"));
        
        // 5. PENDING 상태로 참여자 추가
        ChatRoomUserEntity newUser = ChatRoomUserEntity.builder()
            .chatRoom(requester.getChatRoom())
            .member(invitedMember)
            .role("MEMBER")
            .invitationStatus("PENDING")
            .lastReadMessageId(0L)
            .lastReadMessageCount(0L)
            .isFavorite(0)
            .build();
        
        chatRoomUserRepository.save(newUser);
        
        log.info("✅ 초대 완료: invitedMemberId={}, status=PENDING", invitedMemberId);
    }
    
    // [초대] 초대 수락
    @Override
    public void acceptInvitation(Long roomId, Long memberId) {
        log.info("✅ [초대 수락] roomId: {}, memberId: {}", roomId, memberId);
        
        // 1. 참여자 정보 조회
        ChatRoomUserEntity roomUser = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("초대 정보를 찾을 수 없습니다"));
        
        // 2. PENDING 상태인지 확인
        if (!"PENDING".equals(roomUser.getInvitationStatus())) {
            throw new IllegalArgumentException("처리할 수 있는 초대가 아닙니다");
        }
        
        // 3. 상태를 ACCEPTED로 변경
        roomUser.setInvitationStatus("ACCEPTED");
        chatRoomUserRepository.save(roomUser);
        
        // 4. 시스템 메시지 전송: "OO님이 들어왔습니다"
        String memberName = roomUser.getMember().getName();
        saveSystemMessage(roomUser.getChatRoom(), memberName + "님이 들어왔습니다.");
        
        log.info("✅ 초대 수락 완료: memberId={}, memberName={}", memberId, memberName);
    }
    
    // [초대] 초대 거절
    @Override
    public void rejectInvitation(Long roomId, Long memberId) {
        log.info("❌ [초대 거절] roomId: {}, memberId: {}", roomId, memberId);
        
        // 1. 참여자 정보 조회
        ChatRoomUserEntity roomUser = chatRoomUserRepository
            .findByChatRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new IllegalArgumentException("초대 정보를 찾을 수 없습니다"));
        
        // 2. PENDING 상태인지 확인
        if (!"PENDING".equals(roomUser.getInvitationStatus())) {
            throw new IllegalArgumentException("처리할 수 있는 초대가 아닙니다");
        }
        
        // 3. 참여 레코드 삭제 (거절 시엔 레코드를 제거)
        chatRoomUserRepository.delete(roomUser);
        
        log.info("✅ 초대 거절 완료: memberId={}, 레코드 삭제됨", memberId);
    }
}
