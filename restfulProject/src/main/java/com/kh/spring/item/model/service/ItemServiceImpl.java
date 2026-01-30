package com.kh.spring.item.model.service;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.item.model.dao.ItemDao;
import com.kh.spring.item.model.vo.ItemVO;
import com.kh.spring.item.model.vo.UserItemList;
import com.kh.spring.item.model.vo.UserItemsVO;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private ItemDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	//포인트상점 아이템 조회
	@Override
	public List<ItemVO> storeItem() {
		
		return dao.storeItem(sqlSession);
	}
	
	//포인트상점 보유중인 아이템 조회
	@Override
	public List<ItemVO> storeMyItem(int memberId) {
		
		return dao.storeMyItem(sqlSession,memberId);
	}
	
	//전체 아이템 중 특정 하나 조회
	@Override
	public ItemVO itemsDetail(int itemId) {
		
		return dao.itemsDetail(sqlSession,itemId);
	}
		
	//보유 아이템 중 특정 하나 조회
	@Override
	public UserItemList myItemsDetail(HashMap map) {

		return dao.myItemsDetail(sqlSession,map);
	}
	
	//보유중인 아이템 수 조회
	@Override
	public int itemCount(int memberId) {
		
		return dao.itemCount(sqlSession,memberId);
	}
	
	//카테고별 아이템 조회
	@Override
	public List<ItemVO> itemCategories(String category) {
		
		return dao.itemCategories(sqlSession,category);
	}
	
	//포인트상점 아이템 구매
	@Override
	public int buyItem(UserItemsVO userItemsVO) {
		
		return dao.buyItem(sqlSession,userItemsVO);
	}
	
	
	 
	

	
	
}
