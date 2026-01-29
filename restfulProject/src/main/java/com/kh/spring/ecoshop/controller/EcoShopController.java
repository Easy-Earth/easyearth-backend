package com.kh.spring.ecoshop.controller;


import com.kh.spring.ecoshop.service.EcoShopService;
import com.kh.spring.ecoshop.vo.EcoShop;
import com.kh.spring.ecoshop.vo.Review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name="EcoShop 관련 Controller", description = "EcoShop 관련 Controller")
@RequestMapping("/eco")
@Controller
public class EcoShopController {

    @Autowired
    private EcoShopService ecoShopService;

    @PostMapping("/review")
    @Operation(summary = "리뷰 등록 Controller" , description = ".")
    public ResponseEntity<?> reviewInsert(Review review) {
        int result = ecoShopService.reviewInsert(review);
        if(result > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 등록 성공");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록 실패");
        }
    }
}
