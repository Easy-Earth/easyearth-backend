package com.kh.spring.inquiries.model.service;

import java.util.Map;

public interface InquiriesService {

	//건의글 등록
	int inquiriesInsert(Map<String, Object> map);

	//건의글 수정
	int inquiriesUpdate(Map<String, Object> map);

	//건의글 삭제
	int inquiriesDelete(Map<String, Object> map);

}
