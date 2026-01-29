package com.kh.spring.ecoshop.service;

import com.kh.spring.ecoshop.dao.EcoShopDao;
import com.kh.spring.ecoshop.vo.EcoShop;
import com.kh.spring.ecoshop.vo.Review;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EcoShopService {

    @Autowired
    private EcoShopDao ecoShopDao;

    @Autowired
    private SqlSessionTemplate sqlSession;

    public Long findEscId(String themeId) {
        return ecoShopDao.getEscIdByThemeId(sqlSession, themeId);
    }

    @Transactional
    public int insertEcoShop(EcoShop ecoShop) {
        int count = ecoShopDao.checkDuplicate(sqlSession, ecoShop.getContsId());
        if (count == 0) {
            return ecoShopDao.insertEcoShop(sqlSession, ecoShop);
        }
        return 0;
    }

    public int reviewInsert(Review review) {
        return ecoShopDao.reviewInsert(sqlSession, review);
    }
}