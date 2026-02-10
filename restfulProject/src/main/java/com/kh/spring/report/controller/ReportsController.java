package com.kh.spring.report.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.attendance.controller.AttendanceController;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.member.model.vo.MemberVO;
import com.kh.spring.report.model.service.ReportsService;
import com.kh.spring.report.model.vo.ReportsListDTO;
import com.kh.spring.report.model.vo.ReportsVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/reports")
@Slf4j
@Tag(name = "신고", description = "신고")
public class ReportsController {

    private final AttendanceController attendanceController;

	@Autowired
	private ReportsService service;

    ReportsController(AttendanceController attendanceController) {
        this.attendanceController = attendanceController;
    }
	
	@Operation(summary = "신고 목록 조회", description = "전체 목록 || 검색 목록 || 필터링 목록")
	@GetMapping("/list")
	public ResponseEntity<ReportsListDTO> reportsList(
			@RequestParam(value="page", defaultValue = "1") int currentPage,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String condition,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String status
	) {
		
		HashMap<String, String> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("type", type);
		map.put("status", status);
		
		int listCount = 0;
		int boardLimit = 5;
		int pageLimit = size;
		
		if (keyword != null && !keyword.isEmpty()) {
			listCount = service.searchReportsCount(map);  //검색된 개수
		}else if ((type != null && !type.isEmpty()) || 
					(status != null && !status.isEmpty())) {
			listCount = service.filterReportsCount(map);  //필터링된 개수
		}else {
			listCount = service.reportListsCount();  //전체 개수
		}
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 5, size);
		
		ArrayList<ReportsVO> list;
		
		if (keyword != null && !keyword.isEmpty()) {
			list = service.searchReportsList(map, pi);
		}else if ((type != null && !type.isEmpty()) || 
					(status != null && !status.isEmpty())) {
			list = service.filterReportsList(map, pi);
		}else {
			list = service.reportsList(pi);
		}
		
		return ResponseEntity.ok(ReportsListDTO.of(list, pi));
	}
	
	//신고글 상세보기
	@Operation(summary = "신고글 상세보기", description = "신고글 상세보기")
	@GetMapping("/detail/{reportsId}")
	public ResponseEntity<?> reportsDetail(@PathVariable int reportsId) {
		
		try {
			ReportsVO reports = service.reportsDetail(reportsId);
			
			if(reports != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("reports", reports);
				
				return ResponseEntity.ok(map);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
									 .body("해당 신고 내역을 찾을 수 없습니다.");
			}
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("조회 중 오류 발생");
		}
	}
	
	@Operation(summary = "신고 등록", description = "신고 등록")
	@PostMapping("/insert")
	public ResponseEntity<?> reportsInsert (@RequestParam int memberId,
										    @RequestParam int targetMemberId,
										    @RequestParam String type,
										    @RequestParam(value="postId", required=false, defaultValue="0") int postId,
										    @RequestParam(value="replyId", required=false, defaultValue="0") int replyId,
										    @RequestParam(value="reviewId", required=false, defaultValue="0") int reviewId,
										    @RequestParam String reason,
										    @RequestParam String detail
	) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("targetMemberId", targetMemberId);
		map.put("type", type);
		map.put("postId", postId);
		map.put("replyId", replyId);
		map.put("reviewId", reviewId);
		map.put("reason", reason);
		map.put("detail", detail);
		
		int result = service.reportsInsert(map);
		
		if(result > 0) {
			return ResponseEntity.ok("신고 등록 성공");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 			 .body("신고 등록 실패");
		}
	}

	@Operation(summary = "신고 수정", description = "신고 수정")
	@PutMapping("/update")
	public ResponseEntity<?> reportsUpdate(@RequestParam int reportsId,
										   @RequestParam int memberId,
										   @RequestParam(required = false) String reason,
										   @RequestParam(required = false) String detail
	) {
		ReportsVO reports = ReportsVO.builder()
					                .reportsId(reportsId)
					                .memberId(memberId)
					                .reason(reason)
					                .detail(detail)
					                .build();
		
		int result = service.reportsUpdate(reports);
		
		if (result > 0) {
			return ResponseEntity.ok("신고 수정 성공");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("수정 가능한 상태가 아닙니다.");
		}
	}
	
	@Operation(summary = "신고 삭제", description = "신고 삭제")
	@DeleteMapping("/delete")
	public ResponseEntity<?> reportsDelete(@RequestParam int reportsId,
										   @RequestParam int memberId
	) {
		
		ReportsVO reports = ReportsVO.builder()
									 .reportsId(reportsId)
									 .memberId(memberId)
									 .build();
		
		int result = service.reportsDelete(reports);
		
		if (result > 0) {
			return ResponseEntity.ok("신고 삭제 성공");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("삭제 가능한 상태가 아닙니다.");
		}
	}
	
	//신고글 상태 처리 - 관리자 권한
	@Operation(summary = "(관리자) 신고글 상태 처리", description = "(관리자) 신고글 상태 처리")
	@PutMapping("/changeStatus")
	public ResponseEntity<?> reportsStatus(@RequestParam int memberId,
										   @RequestParam int reportsId,
										   @RequestParam String status
	) {
		if(memberId != 1) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
								 .body("처리 권한이 없습니다.");
		}
		
		try {
			int result = service.reportsStatus(reportsId, status);
			
			if (result > 0) {
				return ResponseEntity.ok("처리 완료");
			}else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
									 .body("잘못된 요청입니다.");
			}
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					 .body("처리 중 오류 발생");
		}
	}
	
	//누적 신고 10회 블라인드 처리 
	@Operation(summary = "누적 신고 10회 블라인드 처리", description = "누적 신고 10회 블라인드 처리")
	@PutMapping("/blind")
	public ResponseEntity<?> reportsBlind(@RequestParam String type,
									      @RequestParam(value="postId", required=false, defaultValue="0") int postId,
									      @RequestParam(value="replyId", required=false, defaultValue="0") int replyId,
									      @RequestParam(value="reviewId", required=false, defaultValue="0") int reviewId
	) {

		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("postId", postId);
		map.put("replyId", replyId);
		map.put("reviewId", reviewId);
		
		try {
			int result = service.reportsBlind(map);
			
			if(result > 0) {
				return ResponseEntity.ok("누적 신고 10회 : 블라인트 처리 완료");
			}else {
				return ResponseEntity.ok("조건 미달 : 누적 횟수 10회 미만입니다.");
			}
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("블라인드 자동 처리 중 오류 발생");
		}
	}
	
	
}
