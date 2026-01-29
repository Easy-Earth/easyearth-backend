package com.kh.spring.ecoshop.dao;

import com.kh.spring.ecoshop.vo.EcoShop;
import com.kh.spring.ecoshop.vo.Review;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EcoShopDao {

    public Long getEscIdByThemeId(SqlSessionTemplate sqlSession, String themeId) {
        return sqlSession.selectOne("ecoShopMapper.getEscIdByThemeId", themeId);
    }

    public int checkDuplicate(SqlSessionTemplate sqlSession, String contsId) {
        return sqlSession.selectOne("ecoShopMapper.checkDuplicate", contsId);
    }

    public int insertEcoShop(SqlSessionTemplate sqlSession, EcoShop ecoShop) {
        return sqlSession.insert("ecoShopMapper.insertEcoShop", ecoShop);
    }

    public int reviewInsert(SqlSessionTemplate sqlSession, Review review) {
        return sqlSession.insert("ecoShopMapper.reviewInsert", review);
    }
}