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

	public MemberVO loginMember(SqlSession sqlSession, MemberVO m) {
		
		return sqlSession.selectOne("memberMapper.loginMember",m);
	}

}
