package com.kh.spring.item.model.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.spring.item.model.vo.RandomPullHistory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.item.model.vo.ItemVO;
import com.kh.spring.item.model.vo.UserItemList;
import com.kh.spring.item.model.vo.UserItemsVO;

@Repository
public class ItemDao {

	//포인트상점 아이템 조회
	public List<ItemVO> storeItem(SqlSession sqlSession) {
		
		return sqlSession.selectList("itemMapper.storeItem");
	}
	
	//포인트상점 보유중인 아이템 조회
	public List<ItemVO> storeMyItem(SqlSessionTemplate sqlSession,int memberId) {
		
		return sqlSession.selectList("itemMapper.storeMyItem",memberId);
	}
	
	//전체 아이템 중 특정 하나 조회
	public ItemVO itemsDetail(SqlSessionTemplate sqlSession, int itemId) {
//		ItemVO item = sqlSession.selectOne("itemMapper.itemsDetail",itemId);
//		System.out.print(item.getCategory());
		return sqlSession.selectOne("itemMapper.itemsDetail",itemId);
	}

	//보유 아이템 중 특정 하나 조회
	public UserItemList myItemsDetail(SqlSessionTemplate sqlSession, HashMap map) {
		
		return sqlSession.selectOne("itemMapper.myItemsDetail",map);
	}
	
	//보유중인 아이템 수 조회
	public int itemCount(SqlSessionTemplate sqlSession, int memberId) {
		
		return sqlSession.selectOne("itemMapper.itemCount",memberId);
	}

	//전체 아이템 중 특정 하나 조회
	public ItemVO itemsDetail(SqlSessionTemplate sqlSession, int itemId) {
//		ItemVO item = sqlSession.selectOne("itemMapper.itemsDetail",itemId);
//		System.out.print(item.getCategory());
		return sqlSession.selectOne("itemMapper.itemsDetail",itemId);
	}

	//보유 아이템 중 특정 하나 조회
	public UserItemList myItemsDetail(SqlSessionTemplate sqlSession, HashMap map) {

		return sqlSession.selectOne("itemMapper.myItemsDetail",map);
	}

	//보유중인 아이템 수 조회
	public int itemCount(SqlSessionTemplate sqlSession, int memberId) {

		return sqlSession.selectOne("itemMapper.itemCount",memberId);
	}

	//카테고별 아이템 조회
	public List<ItemVO> itemCategories(SqlSessionTemplate sqlSession, String category) {
		
		return sqlSession.selectList("itemMapper.itemCategories",category);
	}
	
	//포인트상점 아이템 구매
	public int buyItem(SqlSessionTemplate sqlSession, UserItemsVO userItemsVO) {
		
		return sqlSession.insert("itemMapper.buyItem",userItemsVO);
	}

	public ItemVO randomPull(SqlSessionTemplate sqlSession, String rarity){
		return sqlSession.selectOne("itemMapper.randomPull", rarity);
	}

	//아이템을 MEMBER 테이블에 INSERT
	public int insertItemToMember(SqlSessionTemplate sqlSession, RandomPullHistory randomPullHistory) {
		return sqlSession.insert("itemMapper.insertItemToMember", randomPullHistory);
	}
}
