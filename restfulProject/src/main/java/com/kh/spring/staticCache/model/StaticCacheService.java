package com.kh.spring.staticCache.model;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaticCacheService {

    @Autowired
    private StaticCacheDao dao;

    @Autowired
    private SqlSessionTemplate sqlSession;

    public int environmentEffectPersonal(StaticCacheVO vo) {
        int result = dao.environmentEffectPersonal(sqlSession,vo.getMemberId());

        return 3;
    }
}
