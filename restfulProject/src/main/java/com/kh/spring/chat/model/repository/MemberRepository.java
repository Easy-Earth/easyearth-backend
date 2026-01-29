package com.kh.spring.chat.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.spring.chat.model.vo.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

	//로그인 ID로 회원 찾기
	Optional<MemberEntity> findByLoginId(String loginId);
}
