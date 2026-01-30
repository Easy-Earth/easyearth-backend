package com.kh.spring.item.model.service;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.item.model.dao.ItemDao;
import com.kh.spring.item.model.vo.ItemVO;
import com.kh.spring.item.model.vo.RandomPullHistory;
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
	
	//등급별 아이템 조회
	@Override
	public List<ItemVO> itemRarity(String rarity) {
		
		return dao.itemRarity(sqlSession,rarity);
	}
	
	//포인트상점 아이템 구매
	@Override
	public int buyItem(UserItemsVO userItemsVO) {
		
		return dao.buyItem(sqlSession,userItemsVO);
	}
	
	//아이템 장착/해제
	@Override
    @Transactional
    public int equipItem(int userId, int uiId) {
		
		//상태값 가져와보기
		String status = dao.selectStatus(sqlSession, uiId);
		
		if(status.equals("Y")) {
			
			int result = dao.updateStatus(sqlSession,uiId);
			
			return -1;
		}
		// 1️ 장착하려는 아이템의 카테고리 조회
		String category = dao.selectCategoryByUiId(sqlSession,userId, uiId);
		
		if (category == null) {
		    return -2;
		}
		
		// 2️ 같은 카테고리 기존 장착 아이템 전부 해제
		dao.unequipByCategory(sqlSession,userId, category);
		
		// 3️ 선택한 아이템 장착
		int result = dao.equipItem(sqlSession,userId, uiId);
		
		return result;
		
    }

	
	@Override
	public int randomPull(RandomPullHistory randomPullHistory) {
		ItemVO item = dao.randomPull(sqlSession, randomPullHistory.getRarity());
		if (item != null) {
			// 2. 뽑힌 아이템의 정보를 history 객체에 세팅
			// (memberId는 이미 컨트롤러에서 세팅되어 넘어온 상태입니다)
			randomPullHistory.setItemId(item.getItemId());
			randomPullHistory.setPrice(item.getPrice());
			randomPullHistory.setItemName(item.getName()); // 필요시 추가
			randomPullHistory.setDescription(item.getDescription());
			randomPullHistory.setIsOnSale(item.getIsOnSale());
			randomPullHistory.setCategory(item.getCategory());

			// 3. 중요!! dao에 'item'이 아닌 'randomPullHistory'를 전달
			int result = dao.insertItemToMember(sqlSession, randomPullHistory);
			return result;
		}

		return 0;
	}

}
