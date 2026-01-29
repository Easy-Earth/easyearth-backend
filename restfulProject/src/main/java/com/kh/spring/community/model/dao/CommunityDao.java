package com.kh.spring.community.model.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.community.model.vo.CommunityPostVO;

@Repository
public class CommunityDao {
	
	//게시글 총 개수
	public int listCount(SqlSessionTemplate sqlSession) {
		return sqlSession.selectOne("communityMapper.listCount");
	}
	
	//검색된 게시글 개수
	public int searchListCount(SqlSessionTemplate sqlSession, HashMap<String, String> map) {
		return sqlSession.selectOne("communityMapper.searchListCount");
	}

	//필터링된 게시글 개수
	public int filterListCount(SqlSessionTemplate sqlSession, HashMap<String, String> map) {
		return sqlSession.selectOne("communityMapper.filterListCount");
	}
	
	//게시글 목록 조회
	public ArrayList<CommunityPostVO> communityList(SqlSessionTemplate sqlSession, PageInfo pi) {
		
		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("communityMapper.communityList", null, rowBounds);
	}

	//게시글 검색 조회
	public ArrayList<CommunityPostVO> searchList(SqlSessionTemplate sqlSession, HashMap<String, String> map,
			PageInfo pi) {
		
		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("communityMapper.searchList", map, rowBounds);
	}
	
	//게시글 필터링 조회
	public ArrayList<CommunityPostVO> filterList(SqlSessionTemplate sqlSession, HashMap<String, String> map,
			PageInfo pi) {
		
		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("communityMapper.filterList", map, rowBounds);
	}

	//게시글 등록
	public int communityInsert(SqlSessionTemplate sqlSession, CommunityPostVO cp) {
		return sqlSession.insert("communityMapper.communityInsert",cp);
	}

	//게시글 수정
	public int communityUpdate(SqlSessionTemplate sqlSession, CommunityPostVO cp) {
		return sqlSession.update("communityMapper.communityUpdate",cp);
	}

	//게시글 상세보기
	public CommunityPostVO communityDetail(SqlSessionTemplate sqlSession, int postId) {
		return sqlSession.selectOne("communityMapper.communityDetail", postId);
	}

	//게시글 삭제
	public int communityDelete(SqlSessionTemplate sqlSession, int postId) {
		return sqlSession.delete("communityMapper.communityDelete", postId);
	}




}
