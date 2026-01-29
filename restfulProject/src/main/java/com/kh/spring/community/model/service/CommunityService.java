package com.kh.spring.community.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.community.model.vo.CommunityPostVO;

public interface CommunityService {
	
	
	//게시글 등록
	int communityInsert(CommunityPostVO cp);

	//게시글 수정
	int communityUpdate(CommunityPostVO cp);

	//게시글 상세보기
	CommunityPostVO communityDetail(int postId);

	//게시글 삭제
	int communityDelete(int postId);



}
