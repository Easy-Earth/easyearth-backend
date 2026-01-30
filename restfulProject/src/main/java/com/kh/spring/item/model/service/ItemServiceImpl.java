package com.kh.spring.item.model.service;

import java.util.HashMap;
import java.util.List;

import com.kh.spring.item.model.vo.RandomPullHistory;
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
		System.out.println(category);
		return dao.itemCategories(sqlSession,category);
	}
	
	//포인트상점 아이템 구매
	@Override
	public int buyItem(UserItemsVO userItemsVO) {
		
		return dao.buyItem(sqlSession,userItemsVO);
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

			// 3. 중요!! dao에 'item'이 아닌 'randomPullHistory'를 전달
			int result = dao.insertItemToMember(sqlSession, randomPullHistory);
			return result;
		}

		return 0;
	}

}
