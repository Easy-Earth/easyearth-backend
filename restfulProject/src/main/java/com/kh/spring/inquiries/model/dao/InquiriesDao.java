package com.kh.spring.inquiries.model.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InquiriesDao {

	//건의글 등록
	public int inquiriesInsert(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.insert("inquiriesMapper.inquiriesInsert", map);
	}

	//건이글 수정
	public int inquiriesUpdate(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.update("inquiriesMapper.inquiriesUpdate", map);
	}

	//게시글 삭제
	public int inquiriesDelete(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.delete("inquiriesMapper.inquiriesDelete", map);
	}

}
