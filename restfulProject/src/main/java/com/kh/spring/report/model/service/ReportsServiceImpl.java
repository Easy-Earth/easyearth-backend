package com.kh.spring.report.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.report.model.dao.ReportsDao;
import com.kh.spring.report.model.vo.ReportsVO;

@Service
public class ReportsServiceImpl implements ReportsService{

	@Autowired
	private ReportsDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	//신고글 전체 개수
	@Override
	public int reportListsCount() {
		return dao.reportListsCount(sqlSession);
	}

	//검색된 신고글 개수
	@Override
	public int searchReportsCount(HashMap<String, String> map) {
		return dao.searchReportsCount(sqlSession, map);
	}

	//필터링된 신고글 개수
	@Override
	public int filterReportsCount(HashMap<String, String> map) {
		return dao.filterReportsCount(sqlSession, map);
	}

	//신고글 목록 조회
	@Override
	public ArrayList<ReportsVO> reportsList(PageInfo pi) {
		return dao.reportsList(sqlSession, pi);
	}

	//신고글 검색 조회
	@Override
	public ArrayList<ReportsVO> searchReportsList(HashMap<String, String> map, PageInfo pi) {
		return dao.searchReportsList(sqlSession, map, pi);
	}

	//신고글 필터링 조회
	@Override
	public ArrayList<ReportsVO> filterReportsList(HashMap<String, String> map, PageInfo pi) {
		return dao.filterReportsList(sqlSession, map, pi);
	}
	
	//신고 등록
	@Transactional
	@Override
	public int reportsInsert(Map<String, Object> map) {
		
		int result = dao.reportsInsert(sqlSession, map);
		
		if (result > 0) {
			int reportsCount = dao.reportsCount(sqlSession, map);
			
			if (reportsCount >= 10) {
				dao.disableTarget(sqlSession, map);
			}
		}
		return result;
	}

	//신고 수정
	@Override
	public int reportsUpdate(ReportsVO reports) {
		return dao.reportsUpdate(sqlSession, reports);
	}

	//신고 삭제
	@Override
	public int reportsDelete(ReportsVO reports) {
		return dao.reportsDelete(sqlSession, reports);
	}
}
