package com.kh.spring.chat.model.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.spring.chat.model.vo.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 1. 특정 방, 특정 메시지 ID 이후의 메시지 개수 카운트 (안 읽은 메시지 수)
    long countByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long lastReadMessageId);

    // 2. 무한 스크롤(커서 기반): 특정 메시지(cursorId)보다 이전에 작성된 메시지를 가져옴
    // 첫 로딩 시에는 cursorId가 아주 큰 값(또는 null 처리)이어야 함
    @Query("""
            SELECT m
            FROM ChatMessageEntity m
            WHERE m.chatRoom.id = :chatRoomId
            AND m.id < :cursorId
            ORDER BY m.createdAt DESC
            """)
    Slice<ChatMessageEntity> findByChatRoomIdAndIdLessThan(
            @Param("chatRoomId") Long chatRoomId,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

    // 첫 진입 시 (가장 최신 메시지 N개)
    Slice<ChatMessageEntity> findByChatRoomIdOrderByCreatedAtDesc(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable);
}
