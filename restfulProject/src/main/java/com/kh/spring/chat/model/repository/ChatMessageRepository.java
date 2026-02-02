package com.kh.spring.chat.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.spring.chat.model.vo.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    // 2. 무한 스크롤(커서 기반): 특정 메시지(cursorId)보다 이전에 작성된 메시지를 가져옴
    // 첫 로딩 시에는 cursorId가 아주 큰 값(또는 null 처리)이어야 함
    @org.springframework.data.jpa.repository.Query("SELECT m FROM ChatMessageEntity m WHERE m.chatRoom.id = :chatRoomId AND m.id < :cursorId ORDER BY m.createdAt DESC")
    org.springframework.data.domain.Page<ChatMessageEntity> findByChatRoomIdAndIdLessThan(@org.springframework.data.repository.query.Param("chatRoomId") Long chatRoomId, @org.springframework.data.repository.query.Param("cursorId") Long cursorId, org.springframework.data.domain.Pageable pageable);

    // 첫 진입 시 (가장 최신 메시지 N개)
    org.springframework.data.domain.Page<ChatMessageEntity> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, org.springframework.data.domain.Pageable pageable);
}
