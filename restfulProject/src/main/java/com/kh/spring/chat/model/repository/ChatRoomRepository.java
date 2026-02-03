package com.kh.spring.chat.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.spring.chat.model.vo.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
	
	//'내가 참여한 방 목록' 등의 쿼리 추가 예정

    // 내가 참여한 방 목록 ('ChatRoomUserEntity'와 조인)
    @Query("SELECT r FROM ChatRoomEntity r JOIN ChatRoomUserEntity u ON r.id = u.chatRoom.id WHERE u.member.id = :memberId ORDER BY r.lastMessageAt DESC")
    List<ChatRoomEntity> findChatRoomsByMemberId(@Param("memberId") Long memberId);

}
