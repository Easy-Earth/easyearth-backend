package com.kh.spring.inquiries.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kh.spring.community.controller.CommunityController;
import com.kh.spring.inquiries.model.service.InquiriesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/inquiries")
@Slf4j
@Tag(name = "건의사항", description = "건의사항")
public class InquiriesController {

    private final CommunityController communityController;
	
	@Autowired
	private InquiriesService service;

    InquiriesController(CommunityController communityController) {
        this.communityController = communityController;
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
							+ "memberId : 사용자 아이디 \\n\\n"
							+ "title : 건의글 제목 \n\n"
							+ "content : 건의 내용 \n\n"
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
				description = "inquiriesId : 건의글 번호 \\n\\n"
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
