package com.kh.spring.community.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.community.model.service.CommunityService;
import com.kh.spring.community.model.vo.CommunityListDTO;
import com.kh.spring.community.model.vo.CommunityPostVO;
import com.kh.spring.util.FileUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/community")
@Slf4j
@Tag(name = "커뮤니티", description = "커뮤니티")
public class CommunityController {
	
	@Autowired
	private CommunityService service;
	
	@Autowired
	private FileUtil fileUtil;
	
	//게시글 목록 || 검색 목록 || 필터링 목록
    @Operation(summary = "게시글 목록 조회", description = "전체 목록 || 검색 목록 || 필터링 목록")
	@GetMapping("/list")
	public ResponseEntity<CommunityListDTO> communityList(
			@RequestParam(value="page", defaultValue = "1") int currentPage,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String condition,
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String category) {
		
		int listCount = 0;
		int boardLimit = 5;
		int pageLimit = size;
		
		HashMap<String, String> map = new HashMap<>();
		
		if (keyword != null && !keyword.isEmpty()) {
			map.put("keyword", keyword);
			map.put("condition", condition);
			listCount = service.searchListCount(map); // 검색된 개수
			
		}else if (category != null && !category.isEmpty()) {
			map.put("category", category);
			listCount = service.filterListCount(map); // 필터링된 개수
			
		}else {
			listCount = service.listCount();  //전체 개수
		}
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, boardLimit, pageLimit);
		
		ArrayList<CommunityPostVO> list;
		
		
		if (keyword != null && !keyword.isEmpty()) {
			list = service.searchList(map, pi);
		}else if (category != null && !category.isEmpty()) {
			list= service.filterList(map, pi);
		}else {
			list = service.communityList(pi);
		}
		
		return ResponseEntity.ok(CommunityListDTO.of(list, pi));		
	}
	
	
	
	//게시글 등록
    @Operation(summary = "게시글 등록", description = "게시글 등록")
    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 1. 미디어 타입 명시
    public ResponseEntity<?> communityInsert(
            @ModelAttribute CommunityPostVO cp, // 2. @ModelAttribute 사용
            @RequestPart(value = "uploadFile", required = false) MultipartFile uploadFile // 3. @RequestPart 명시
    ) {
		
		//첨부파일 있는 경우
		if(!uploadFile.getOriginalFilename().equals("")) {
			
			try {
				
				//저장경로 설정 및 저장, 파일명 변경
				String changeName = fileUtil.saveFile(uploadFile);
				String originName = uploadFile.getOriginalFilename();
				
				cp.setChangeName(changeName);
				cp.setOriginName(originName);
				
			}catch(Exception e) {
				
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								     .body("파일 업로드에 실패했습니다.");
			}
			
		}
		
		//게시글 등록처리
		int result = service.communityInsert(cp);
		
		if(result > 0) {
			return ResponseEntity.status(HttpStatus.CREATED)
								 .body("게시글 등록 성공");
		
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("게시글 등록 실패");
		}
		
	}
	
	//게시글 수정
    @Operation(summary = "게시글 수정", description = "게시글 수정")
	@PutMapping("/update")
	public ResponseEntity<?> communityUpdate(CommunityPostVO cp
											, MultipartFile uploadFile) {
		
		String deleteFile = null;
		
		//새로운 첨부파일이 추가되었는지 확인
		if(uploadFile != null && !uploadFile.getOriginalFilename().equals("")) {
			
			//기존 첨부파일이 있는지 없는지 확인
			if(cp.getOriginName()!=null) { 
				deleteFile = cp.getChangeName();
			}
			
			//새로 업로드된 파일 업로드 및 변경된 이름
			String changeName;
			try {
				changeName = fileUtil.saveFile(uploadFile);
				cp.setOriginName(uploadFile.getOriginalFilename()); 
				cp.setChangeName(changeName);
			
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						 .body("파일 업로드 중 오류 발생");
			}
		}
		
		int result = service.communityUpdate(cp);
		
		if(result > 0) {
			return ResponseEntity.ok("게시글 수정 성공");
		
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("게시글 정보 수정 실패");
		}
	}
	
	//게시글 삭제
    @Operation(summary = "게시글 삭제", description = "게시글 삭제")
	@DeleteMapping("/delete/{postId}")
	public ResponseEntity<?> communityDelete(@PathVariable int postId) {
		
		//postId로 게시글 조회
		CommunityPostVO cp = service.communityDetail(postId);
		
		if(cp == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body("게시글을 찾을 수 없습니다.");
		}
		
		//게시글 삭제
		int result = service.communityDelete(postId);
		
		if(result > 0) {
			
			if(cp.getOriginName()!=null) {
							
				boolean flag = fileUtil.deleteFile(cp.getChangeName());
				
				if(!flag) {
					log.warn("정보 삭제는 되었지만 파일 삭제 오류 발생");
				}
			}
			return ResponseEntity.ok("게시글이 삭제되었습니다.");
		
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								 .body("게시글 삭제 처리 중 오류가 발생했습니다.");
		}
	}
	
}
