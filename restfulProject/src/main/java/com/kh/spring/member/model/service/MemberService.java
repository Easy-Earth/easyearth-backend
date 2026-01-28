package com.kh.spring.member.model.service;

import com.kh.spring.member.model.vo.MemberVO;

public interface MemberService {
	
	//회원가입
	int insertMember(MemberVO m);
	
	//로그인
	MemberVO loginMember(MemberVO m);

}
