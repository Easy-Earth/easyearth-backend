package com.kh.spring.member.model.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.MemberVO;

@Repository
public class MemberDao {
	
	//회원가입
	public int insertMember(SqlSession sqlSession, MemberVO m) {
		
		return sqlSession.insert("memberMapper.insertMember",m);
	}
	
	//아이디 중복체크
	public int checkId(SqlSession sqlSession, String loginId) {
		
		return sqlSession.selectOne("memberMapper.checkId",loginId);
	}

	//로그인
	public MemberVO loginMember(SqlSession sqlSession, MemberVO m) {
		
		return sqlSession.selectOne("memberMapper.loginMember",m);
	}
	
	//회원 정보 수정
	public int updateMember(SqlSession sqlSession, MemberVO m) {
		
		return sqlSession.update("memberMapper.updateMember",m);
	}

	//회원 탈퇴
	public int deleteMember(SqlSession sqlSession, String loginId) {
		
		return sqlSession.delete("memberMapper.deleteMember",loginId);
	}

	//회원 정보 조회
	public MemberVO selectMemberById(SqlSession sqlSession, int memberId) {
		
		return sqlSession.selectOne("memberMapper.selectMemberById",memberId);
	}

	

}
