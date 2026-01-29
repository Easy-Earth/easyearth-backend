package com.kh.spring.community.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.community.model.vo.CommunityPostVO;

@Repository
public class CommunityDao {

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
