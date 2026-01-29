package com.kh.spring.community.model.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.community.model.dao.CommunityDao;
import com.kh.spring.community.model.vo.CommunityPostVO;

@Service
public class CommunityServiceImpl implements CommunityService {
	
	@Autowired
	private CommunityDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	//게시글 등록
	@Override
	public int communityInsert(CommunityPostVO cp) {
		return dao.communityInsert(sqlSession, cp);
	}


	//게시글 수정
	@Override
	public int communityUpdate(CommunityPostVO cp) {
		return dao.communityUpdate(sqlSession, cp);
	}

	//게시글 상세보기
	@Override
	public CommunityPostVO communityDetail(int postId) {
		return dao.communityDetail(sqlSession, postId);
	}

	//게시글 삭제
	@Override
	public int communityDelete(int postId) {
		return dao.communityDelete(sqlSession, postId);
	}


	
}
