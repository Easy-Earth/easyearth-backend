package com.kh.spring.chat.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.spring.chat.model.vo.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    // 특정 채팅방의 메시지를 과거순으로 가져오기 (채팅 내역 로딩용)
    List<ChatMessageEntity> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
