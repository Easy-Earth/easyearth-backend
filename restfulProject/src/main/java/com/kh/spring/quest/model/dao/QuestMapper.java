package com.kh.spring.quest.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.spring.quest.model.vo.Quest;

@Mapper
public interface QuestMapper {

    // 오늘의 퀘스트 목록 조회
    List<Quest> selectDailyQuests();

    // 퀘스트 인증 내역 저장 (QUEST_HISTORY)
    int insertQuestHistory(@org.apache.ibatis.annotations.Param("userId") int userId,
            @org.apache.ibatis.annotations.Param("questNo") int questNo,
            @org.apache.ibatis.annotations.Param("savedFileName") String savedFileName);
}
