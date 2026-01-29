package com.kh.spring.community.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.community.model.dao.CommunityDao;
import com.kh.spring.community.model.vo.CommunityPostVO;

@Service
public class CommunityServiceImpl implements CommunityService {
	
	@Autowired
	private CommunityDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//게시글 총 개수
	@Override
	public int listCount() {
		return dao.listCount(sqlSession);
	}
	
	//검색된 게시글 개수
	@Override
	public int searchListCount(HashMap<String, String> map) {
		return dao.searchListCount(sqlSession, map);
	}
	
	//필터링된 게시글 개수
	@Override
	public int filterListCount(HashMap<String, String> map) {
		return dao.filterListCount(sqlSession, map);
	}

	//게시글 목록 조회
	@Override
	public ArrayList<CommunityPostVO> communityList(PageInfo pi) {
		return dao.communityList(sqlSession, pi);
	}

	//게시글 검색 조회
	@Override
	public ArrayList<CommunityPostVO> searchList(HashMap<String, String> map, PageInfo pi) {
		return dao.searchList(sqlSession, map, pi);
	}

	//게시글 필터링 조회
	@Override
	public ArrayList<CommunityPostVO> filterList(HashMap<String, String> map, PageInfo pi) {
		return dao.filterList(sqlSession, map, pi);
	}

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
