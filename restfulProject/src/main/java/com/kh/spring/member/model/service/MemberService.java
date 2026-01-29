package com.kh.spring.member.model.service;

import com.kh.spring.member.model.vo.MemberVO;

public interface MemberService {
	
	//회원가입
	int insertMember(MemberVO m);
	
	//아이디 중복체크
	int checkId(String loginId);
	
	//로그인
	MemberVO loginMember(MemberVO m);
	
	//회원 정보 수정
	int updateMember(MemberVO m);
	
	//회원 탈퇴
	int deleteMember(String loginId);

	//회원 정보 조회
	MemberVO selectMemberById(int memberId);
	

}
