package com.kh.spring.quest.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.quest.model.dao.QuestMapper;
import com.kh.spring.quest.model.vo.Quest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestMapper questMapper;

    /**
     * 오늘의 퀘스트 목록을 조회합니다.
     */
    public List<Quest> getDailyQuests() {

        List<Quest> allQuests = questMapper.selectDailyQuests();

        if (allQuests != null && !allQuests.isEmpty()) {
            Collections.shuffle(allQuests);

            if (allQuests.size() > 5) {
                return allQuests.subList(0, 5);
            }
        }
        return allQuests;
    }

    @Transactional
    public void certifyQuest(int userId, int questNo, MultipartFile file) {

        // 1. 오늘 이미 해당 퀘스트를 인증했는지 확인 (중복 인증 방지)
        int alreadyCertified = questMapper.countTodayQuestByNo(userId, questNo);
        if (alreadyCertified > 0) {
            throw new RuntimeException("오늘 이미 인증한 퀘스트입니다.");
        }

        // 2. 퀘스트 정보 조회 (포인트 확인용)
        Quest quest = questMapper.selectQuestByNo(questNo);
        if (quest == null) {
            throw new RuntimeException("존재하지 않는 퀘스트입니다.");
        }

        // 3. 포인트 지급 (POINT_WALLET 업데이트)
        int pointResult = questMapper.updateMemberPoints(userId, quest.getPoint());
        if (pointResult <= 0) {
            throw new RuntimeException("포인트 지급 실패");
        }

        // 4. 퀘스트 수행 내역 저장
        questMapper.insertQuestHistory(userId, questNo, null);
    }
}


