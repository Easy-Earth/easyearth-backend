package com.kh.spring.report.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.report.model.vo.ReportsVO;

@Repository
public class ReportsDao {

	//신고글 전체 개수
	public int reportListsCount(SqlSessionTemplate sqlSession) {
		return sqlSession.selectOne("reportsMapper.reportListsCount");
	}

	//검색된 신고글 개수
	public int searchReportsCount(SqlSessionTemplate sqlSession, HashMap<String, String> map) {
		return sqlSession.selectOne("reportsMapper.searchReportsCount", map);
	}

	//필터링된 신고글 개수
	public int filterReportsCount(SqlSessionTemplate sqlSession, HashMap<String, String> map) {
		return sqlSession.selectOne("reportsMapper.filterReportsCount", map);
	}

	//신고글 목록 조회
	public ArrayList<ReportsVO> reportsList(SqlSessionTemplate sqlSession, PageInfo pi) {

		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("reportsMapper.reportsList", null, rowBounds);
	}

	//신고글 검색 조회
	public ArrayList<ReportsVO> searchReportsList(SqlSessionTemplate sqlSession, HashMap<String, String> map,
			PageInfo pi) {
		
		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("reportsMapper.searchReportsList", map, rowBounds);
	}

	//신고글 필터링 조회
	public ArrayList<ReportsVO> filterReportsList(SqlSessionTemplate sqlSession, HashMap<String, String> map,
			PageInfo pi) {

		int limit = pi.getBoardLimit();
		int offset = (pi.getCurrentPage() - 1) * limit;
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return (ArrayList)sqlSession.selectList("reportsMapper.filterReportsList", map, rowBounds);
	}

	//신고 등록
	public int reportsInsert(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.insert("reportsMapper.reportsInsert", map);
	}

	//누적 신고 수
	public int reportsCount(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.selectOne("reportsMapper.reportsCount", map);
	}

	//10회 이상 신고 - status : N 처리
	public int disableTarget(SqlSessionTemplate sqlSession, Map<String, Object> map) {
		return sqlSession.update("reportsMapper.disableTarget", map);
	}

	//신고 수정
	public int reportsUpdate(SqlSessionTemplate sqlSession, ReportsVO reports) {
		return sqlSession.update("reportsMapper.reportsUpdate", reports);
	}

	//신고 삭제
	public int reportsDelete(SqlSessionTemplate sqlSession, ReportsVO reports) {
		return sqlSession.delete("reportsMapper.reportsDelete", reports);
	}
}
