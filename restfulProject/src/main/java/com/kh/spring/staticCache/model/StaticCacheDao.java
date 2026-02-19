package com.kh.spring.staticCache.model;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StaticCacheDao {

    public int environmentEffectPersonal(SqlSessionTemplate sqlSession, int memberId) {
        return sqlSession.selectOne("staticCacheMapper.reportListsCount", memberId);
    }
}
