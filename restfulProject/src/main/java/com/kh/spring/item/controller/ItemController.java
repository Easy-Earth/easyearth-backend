package com.kh.spring.item.controller;


import java.util.List;

import com.kh.spring.item.model.vo.RandomPullHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kh.spring.item.model.service.ItemService;
import com.kh.spring.item.model.vo.ItemVO;
import com.kh.spring.item.model.vo.UserItemsVO;
import com.kh.spring.util.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/items")
@Slf4j
@Tag(name = "아이템 컨트롤러", description = "아이템 관련 전부")
public class ItemController {
	
	@Autowired
	private ItemService service;
	
	//포인트상점 아이템 조회
	@Operation(summary = "상점 아이템 조회", description = "상점 아이템 조회")
	@GetMapping("/select")
	public ResponseEntity<?> storeItem(){
		
		List<ItemVO> list = service.storeItem();
		
		return ResponseEntity.ok(list);
	}
	
	//포인트상점 보유중인 아이템 조회
	@Operation(summary = "보유중인 아이템 조회", description = "보유중인 아이템 조회")
	@GetMapping("/myItems/{memberId}")
	public ResponseEntity<?> storeMyItem(@PathVariable int memberId){
		
		List<ItemVO> list = service.storeMyItem(memberId);
		
		return ResponseEntity.ok(list);
	}
	
	//포인트상점 아이템 카테고리 조회
	@Operation(summary = "상점 카테고리 조회", description = "상점 카테고리 조회")
	@GetMapping("/categories/{category}")
	public ResponseEntity<?> categories(@PathVariable String category) {
		
		List<ItemVO> list = service.itemCategories(category);

		return ResponseEntity.ok(list);
	}
	
	
	//포인트상점 아이템 구매
	@Operation(summary = "상점 아이템 구매", description = "상점 아이템 구매")
	@PostMapping("/buy")
	public ResponseEntity<?> buyItem(@RequestBody UserItemsVO userItemsVO){
		
		int result = service.buyItem(userItemsVO);
		
		if(result>0) {
			
			return ResponseEntity.ok("아이템 구매 성공!");
			
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("포인트가 부족합니다.");
		}
		
	}
	

	
	//아이템 구매시 보유수량 증가 구문 OR 트리거
	
	
	
	
	

	@GetMapping("/random/{memberId}")
	@ResponseBody
	@Operation(summary = "랜덤뽑기 API", description = "랜덤뽑기 API")
	public ResponseEntity<?> randomPull(@RequestBody RandomPullHistory randomPullHistory) {

		int randomNum = (int) (Math.random() * 100) + 1;
		//1~69 : COMMON 69%
		//70~94 : RARE  25%
		//95~99 : EPIC 5%
		//100 : LEGENDARY 1%
		if (randomNum <= 69) randomPullHistory.setRarity("COMMON");
		else if (randomNum <= 94) randomPullHistory.setRarity("RARE");
		else if (randomNum <= 99) randomPullHistory.setRarity("EPIC");
		else randomPullHistory.setRarity("LEGENDARY");

		return null;

	}

}
