package com.kh.spring.quest.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quest {
    private int questNo;
    private String questTitle; // 퀘스트 내용 (예: 종이컵 사용 금지)
    private String category; // 집, 회사, 학교 등
    private int point; // 10
}
