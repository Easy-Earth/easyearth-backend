package com.kh.spring.member.model.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.MemberVO;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	private MemberDao dao;
	
	@Autowired
	private SqlSession sqlSession;
	
	//회원가입
	@Override
	public int insertMember(MemberVO m) {
		
		return dao.insertMember(sqlSession,m);
	}
	
	//아이디 중복체크
	@Override
	public int checkId(String loginId) {
		
		return dao.checkId(sqlSession,loginId);
	}
	
	//로그인
	@Override
	public MemberVO loginMember(MemberVO m) {
		
		return dao.loginMember(sqlSession,m);
	}
	
	//회원 정보 수정
	@Override
	public int updateMember(MemberVO m) {
		
		return dao.updateMember(sqlSession,m);
	}
	
	//회원 탈퇴
	@Override
	public int deleteMember(String loginId) {
		
		return dao.deleteMember(sqlSession,loginId);
	}
	
	//회원 정보 조회
	@Override
	public MemberVO selectMemberById(int memberId) {
		
		return dao.selectMemberById(sqlSession,memberId);
	}

	@Override
	public List<Integer> equippedItem(String memberId) {
		return dao.equippedItem(sqlSession, memberId);
	}


}
