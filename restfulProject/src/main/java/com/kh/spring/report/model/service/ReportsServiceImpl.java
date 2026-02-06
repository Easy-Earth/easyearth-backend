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

import lombok.extern.slf4j.Slf4j;

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
	
	//신고글 상세보기
	@Override
	public ReportsVO reportsDetail(int reportsId) {
		return dao.reportsDetail(sqlSession, reportsId);
	}
	
	//신고 등록
	@Transactional
	@Override
	public int reportsInsert(Map<String, Object> map) {
		
		return dao.reportsInsert(sqlSession, map);
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

	//신고글 상태 처리 - 관리자 권한
	@Override
	public int reportsStatus(int reportsId, String status) {
		return dao.reportsStatus(sqlSession, reportsId, status);
	}

	//누적 신고 10회 블라인드 처리 
	@Override
	@Transactional
	public int reportsBlind(Map<String, Object> map) {
		
		// 1. MyBatis를 거치지 않고 Map에 뭐가 들었는지 강제로 다 까보기
	    System.err.println("=== [자바 데이터 체크] ===");
	    map.forEach((key, value) -> System.err.println(key + " : " + value + " (타입: " + value.getClass().getSimpleName() + ")"));
		
		int resolvedCount = dao.selectResolvedCount(sqlSession, map);
		
		if(resolvedCount >= 10) {
			return dao.reportsBlind(sqlSession, map);
		}
		return 0;
	}
}
