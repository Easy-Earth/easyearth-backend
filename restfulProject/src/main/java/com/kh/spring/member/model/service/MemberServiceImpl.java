package com.kh.spring.member.model.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.MemberVO;

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
	
	@Override
	public MemberVO loginMember(MemberVO m) {
		
		return dao.loginMember(sqlSession,m);
	}
	
	

}
