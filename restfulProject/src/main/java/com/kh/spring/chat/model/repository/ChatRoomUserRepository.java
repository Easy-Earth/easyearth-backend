package com.kh.spring.chat.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.spring.chat.model.vo.ChatRoomUserEntity;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUserEntity, Long> {
    // 특정 방에 특정 유저가 참여 중인지 확인
    Optional<ChatRoomUserEntity> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
}