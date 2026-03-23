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
	public List<UserItemList> storeMyItem(int memberId) {
		
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
	@Transactional
	public int buyItem(UserItemsVO userItemsVO) {
		
		int memberId = userItemsVO.getUserId();
		int price = userItemsVO.getPrice();
		
		HashMap<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("price", price);
		
		// 1. 포인트 차감을 먼저 시도합니다.
		// (매퍼에서 AND NOW_POINT >= #{price} 조건이 있다면 포인트 부족 시 0이 리턴됨)
		int result = dao.deductItemPoint(sqlSession, map);
		
		// 2. 차감 성공(result == 1)했을 때만 아이템을 지급합니다.
		if(result > 0) {
			return dao.buyItem(sqlSession, userItemsVO);
		} else {
			// 포인트가 부족하면 0을 리턴하여 컨트롤러에서 실패 처리하게 함
			return 0;
		}
	}
	
	//아이템 장착/해제
	@Override
    @Transactional
    public int equipItem(int userId, int itemId) {
		
		//상태값 가져와보기
		String status = dao.selectStatus(sqlSession, userId, itemId);
		System.out.println(status);
		
		if("Y".equals(status)) {
			
			int result = dao.updateStatus(sqlSession,userId, itemId);
			
			return -1;
		}
		// 1️ 장착하려는 아이템의 카테고리 조회
		String category = dao.selectCategoryByUiId(sqlSession,userId, itemId);
		System.out.println("categroy : " + category);
		if (category == null) {
		    return -2;
		}
		
		// 2️ 같은 카테고리 기존 장착 아이템 전부 해제
		int s = dao.unequipByCategory(sqlSession,userId, category);
		System.out.println("s : " + s);
		// 3️ 선택한 아이템 장착
		int result = dao.equipItem(sqlSession,userId, itemId);
		
		System.out.println(result);
		
		return result;
		
    }


	@Override
	@Transactional
	public int randomPull(RandomPullHistory randomPullHistory) {
		// 1. 포인트 차감
		int payResult = dao.deductPoint(sqlSession, randomPullHistory.getMemberId());
		if (payResult <= 0) {
			throw new RuntimeException("포인트가 부족합니다. (최소 1000P 필요)");
		}

		// 2. 등급에 맞는 랜덤 아이템 조회
		ItemVO item = dao.randomPull(sqlSession, randomPullHistory.getRarity());
		if (item == null) {
			throw new RuntimeException("해당 등급의 아이템 조회에 실패했습니다.");
		}

		// 3. 뽑힌 아이템 정보 세팅
		randomPullHistory.setItemId(item.getItemId());
		randomPullHistory.setPrice(item.getPrice());
		randomPullHistory.setItemName(item.getName());
		randomPullHistory.setDescription(item.getDescription());
		randomPullHistory.setIsOnSale(item.getIsOnSale());
		randomPullHistory.setCategory(item.getCategory());

		// 4. 중복 체크
		int count = dao.checkDuplicateItem(sqlSession, randomPullHistory);

		if (count > 0) {
			// 5-1. 중복이면 500포인트 환급
			int pointResult = dao.addPoint(sqlSession, randomPullHistory.getMemberId());
			if (pointResult <= 0) {
				throw new RuntimeException("중복 보상 처리에 실패했습니다.");
			}
			return 2; // 중복 보상 성공
		} else {
			// 5-2. 신규면 USER_ITEMS에 추가
			int insertResult = dao.insertItemToMember(sqlSession, randomPullHistory);
			if (insertResult <= 0) {
				throw new RuntimeException("아이템 지급에 실패했습니다.");
			}
			return 1; // 신규 아이템 획득 성공
		}
	}

}
