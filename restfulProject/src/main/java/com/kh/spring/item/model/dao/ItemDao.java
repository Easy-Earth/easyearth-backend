package com.kh.spring.item.model.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.item.model.vo.ItemVO;
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

	//카테고별 아이템 조회
	public List<ItemVO> itemCategories(SqlSessionTemplate sqlSession, String category) {
		System.out.println(category);
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
	public int insertItemToMember(SqlSessionTemplate sqlSession, ItemVO itemVo) {
		return sqlSession.insert("itemMapper.insertItemToMember", itemVo);
	}
}
