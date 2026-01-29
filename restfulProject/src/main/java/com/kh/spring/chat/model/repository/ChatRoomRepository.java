package com.kh.spring.chat.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.spring.chat.model.vo.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
	
	//'내가 참여한 방 목록' 등의 쿼리 추가 예정

}
