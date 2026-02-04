package com.kh.spring.ecoshop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReviewerName {
//    SHOP_ID, RATING, CONTENT, NAME
    int shopId;
    String rating;
    String content;
    String name;
    String memberId;
}
