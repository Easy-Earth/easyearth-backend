package com.kh.spring.community.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommunityReplyVO {
	
//	REPLY_ID	NUMBER	No		1	댓글 고유 ID (PK)
//	POST_ID	NUMBER	No		2	연결된 게시글 ID (FK)
//	MEMBER_ID	NUMBER	No		3	댓글 작성자 ID (FK)
//	PARENT_REPLY_ID	NUMBER	Yes		4	부모 댓글 ID (대댓글용, Self-FK)
//	CONTENT	CLOB	No		5	댓글 내용
//	LIKE_COUNT	NUMBER	No	0 	6	댓글 좋아요 수
//	REPORT_COUNT	NUMBER	No	0 	7	댓글 신고 누적 수
//	CREATED_AT	DATE	No	SYSDATE 	8	작성 일시
//	UPDATED_AT	DATE	No	SYSDATE 	9	수정 일시
	
	private int replyId;
	private int postId;
	private int memberId;
	private int parentReplyId;
	private String content;
	private int likeCount;
	private int reportCount;
	private Date createdAt;
	private Date updatedAt;
	
}
