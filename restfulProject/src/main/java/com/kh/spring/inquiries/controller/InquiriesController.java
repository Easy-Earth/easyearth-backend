package com.kh.spring.inquiries.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.community.model.vo.CommunityPostVO;
import com.kh.spring.community.model.vo.PostFilesVO;
import com.kh.spring.inquiries.model.service.InquiriesService;
import com.kh.spring.inquiries.model.vo.InquiriesListDTO;
import com.kh.spring.inquiries.model.vo.InquiriesVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/inquiries")
@Slf4j
@Tag(name = "건의사항", description = "건의사항")
public class InquiriesController {

	@Autowired
	private InquiriesService service;

	//건의글 목록 조회 
	@Operation(summary = "건의글 목록 조회",
			description = "condition : title(제목) / writer(작성자) / content(내용)   \n\n"
						+ "keyword : condition에 대한 검색어   \n\n"
						+ "status(문의 상태) : SUBMITTED(접수 완료) / PROCESSING(진행 중) / COMPLETED(답변 완료)   \n\n")
	@GetMapping("/post/list")
	public ResponseEntity<?> inquiriesList(@RequestParam(value="page", defaultValue = "1") int currentPage,
								           @RequestParam(defaultValue = "10") int size,
								           @RequestParam(required = false) String condition,
								           @RequestParam(required = false) String keyword,
								           @RequestParam(required = false) String status) {
		
		int listCount = 0;
		int boardLimit = 5;
		int pageLimit = size;
		
		HashMap<String, Object> map = new HashMap<>();
		
		if (keyword != null && !keyword.isEmpty()) {
			map.put("keyword", keyword);
			map.put("condition", condition);
			listCount = service.searchListCount(map); // 검색된 개수
			
		}else if (status != null && !status.isEmpty()) {
			map.put("status", status);
			listCount = service.filterListCount(map); // 필터링된 개수
			
		}else {
			listCount = service.listCount();  //전체 개수
		}
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, boardLimit, pageLimit);
		
		ArrayList<InquiriesVO> list;
		
		if (keyword != null && !keyword.isEmpty()) {
			list = service.searchList(map, pi);
		}else if (status != null && !status.isEmpty()) {
			list= service.filterList(map, pi);
		}else {
			list = service.inquiriesList(pi);
		}
		
		return ResponseEntity.ok(InquiriesListDTO.of(list, pi));	
	}

	//건의글 상세보기
	@Operation(summary = "건의글 상세보기", description = "inquiriesId : 조회할 건의글 번호  \n\n"
													+ "loginId : 로그인된 사용자 번호")
	@GetMapping("/post/detail/{inquiriesId}")
	public ResponseEntity<?> inquiriesDetail(@PathVariable int inquiriesId,
											 @RequestParam int loginId) {
		
		int result = service.increaseCount(inquiriesId);
		
		if(result > 0) {
			InquiriesVO inquiry = service.selectInquiry(inquiriesId);
			
			if (inquiry != null) {

				String isPublic = inquiry.getIsPublic();
				
				if(isPublic.equals("N")) {
					if(loginId != 1 && inquiry.getMemberId() != loginId) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN)
											 .body("비공개 건의글은 작성자와 관리자만 확인할 수 있습니다.");
					}
				}
				return ResponseEntity.ok(inquiry);
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
									 .body("존재하지 않는 건의글입니다.");
			}
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body("건의글 조회 중 오류 발생");
	}
	
	//건의글 등록
	@Operation(summary = "건의글 등록", 
			description = "memberId : 사용자 아이디 \n\n"
						+ "title : 건의글 제목 \n\n"
						+ "content : 건의 내용 \n\n"
						+ "isPublic(전체 공개 여부) : Y / N  \n\n"
						+ "isFaq(자주 묻는 질문) : Y / N  \n\n")
	@PostMapping("/post/insert")
	public ResponseEntity<?> inquiriesInsert(@RequestParam int memberId,
											 @RequestParam String title,
											 @RequestParam String content,
											 @RequestParam String isPublic,
											 @RequestParam String isFaq) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("title", title);
		map.put("content", content);
		map.put("isPublic", isPublic);
		map.put("isFaq", isFaq);
		
		int result = service.inquiriesInsert(map);
		
		if (result > 0) {
			return ResponseEntity.ok("건의글 등록 성공");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("건의글 등록 중 오류 발생");
		}
	}
	
	//건의글 수정
	@Operation(summary = "건의글 수정", 
				description = "inquiriesId : 건의글 번호 \n\n"
							+ "memberId : 사용자 아이디  \n\n"
							+ "title : 건의글 제목  \n\n"
							+ "content : 건의 내용  \n\n"
							+ "isPublic(전체 공개 여부) : Y / N  \n\n"
							+ "isFaq(자주 묻는 질문) : Y / N  \n\n")
	@PutMapping("/post/update/{inquiriesId}")
	public ResponseEntity<?> inquiriesUpdate(@PathVariable int inquiriesId,
											 @RequestParam int memberId,
											 @RequestParam String title,
											 @RequestParam String content,
											 @RequestParam String isPublic,
											 @RequestParam String isFaq) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("inquiriesId", inquiriesId);
		map.put("memberId", memberId);
		map.put("title", title);
		map.put("content", content);
		map.put("isPublic", isPublic);
		map.put("isFaq", isFaq);
		
		int result = service.inquiriesUpdate(map);
		
		if (result > 0) {
			return ResponseEntity.ok("건의글 수정 성공");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("건의글 수정 처리 중 오류 발생");
		}
	}

	//건의글 삭제
	@Operation(summary = "건의글 삭제", 
				description = "inquiriesId : 건의글 번호  \n\n"
							+ "memberId : 사용자 아이디")
	@DeleteMapping("/post/delete/{inquiriesId}")
	public ResponseEntity<?> inquiriesDelete(@PathVariable int inquiriesId,
											 @RequestParam int memberId) {
		
		Map<String, Object> map = new HashMap<>();
		map.put("inquiriesId", inquiriesId);
		map.put("memberId", memberId);
		
		int result = service.inquiriesDelete(map);
		
		if (result > 0) {
			return ResponseEntity.ok("건의글 삭제 성공");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("건의글 삭제 처리 중 오류 발생");
		}
		
	}

}
