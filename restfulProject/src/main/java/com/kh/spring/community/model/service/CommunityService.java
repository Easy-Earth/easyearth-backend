package com.kh.spring.community.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.community.model.vo.CommunityPostVO;

public interface CommunityService {
	
	//게시글 총 개수
	int listCount();
	
	//검색된 게시글 개수
	int searchListCount(HashMap<String, String> map);

	//필터링된 게시글 개수
	int filterListCount(HashMap<String, String> map);

	//게시글 목록 조회
	ArrayList<CommunityPostVO> communityList(PageInfo pi);
	
	//게시글 검색 조회
	ArrayList<CommunityPostVO> searchList(HashMap<String, String> map, PageInfo pi);

	//게시글 필터링 조회
	ArrayList<CommunityPostVO> filterList(HashMap<String, String> map, PageInfo pi);
	
	//게시글 등록
	int communityInsert(CommunityPostVO cp);

	//게시글 수정
	int communityUpdate(CommunityPostVO cp);

	//게시글 상세보기
	CommunityPostVO communityDetail(int postId);

	//게시글 삭제
	int communityDelete(int postId);





}
