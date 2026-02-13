package com.kh.spring.chat.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kh.spring.chat.model.vo.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    java.util.Optional<MemberEntity> findByLoginId(String loginId);
}
