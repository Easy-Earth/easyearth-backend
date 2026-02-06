package com.kh.spring.inquiries.model.service;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.inquiries.model.dao.InquiriesDao;

@Service
public class InquiriesServiceImpl implements InquiriesService {

	@Autowired
	private InquiriesDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	//건의글 등록
	@Override
	@Transactional
	public int inquiriesInsert(Map<String, Object> map) {
		return dao.inquiriesInsert(sqlSession, map);
	}

	//건의글 수정
	@Override
	public int inquiriesUpdate(Map<String, Object> map) {
		return dao.inquiriesUpdate(sqlSession, map);
	}

	//건의글 삭제
	@Override
	public int inquiriesDelete(Map<String, Object> map) {
		return dao.inquiriesDelete(sqlSession, map);
	}
	
	
}
