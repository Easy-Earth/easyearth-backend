package com.kh.spring.item.model.service;

import java.util.List;

import com.kh.spring.item.model.vo.ItemVO;
import com.kh.spring.item.model.vo.UserItemsVO;

public interface ItemService {
	
	//포인트상점 아이템 조회
	List<ItemVO> storeItem();
	
	//포인트상점 보유중인 아이템 조회
	List<ItemVO> storeMyItem(int memberId);
	
	//카테고별 아이템 조회
	List<ItemVO> itemCategories(String category);

	//포인트상점 아이템 구매
	int buyItem(UserItemsVO vo);


}
