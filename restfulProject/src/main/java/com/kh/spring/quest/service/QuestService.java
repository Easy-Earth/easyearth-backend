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
     * 날짜 기반 시드를 사용하여 하루 동안은 동일한 5문항이 유지됩니다.
     */
    public List<Quest> getDailyQuests() {

        // [테스트 시 주석 해제하여 사용 - 하루 1회 제한 로직]
        /*
         * int userId = 1;
         * int todayCompleted = questMapper.countTodayQuests(userId);
         * if (todayCompleted >= 5) {
         * // 이미 5개 이상 수행했다면 빈 리스트나 메시지 처리 가능
         * // return Collections.emptyList();
         * }
         */

        List<Quest> allQuests = questMapper.selectDailyQuests();

        if (allQuests != null && !allQuests.isEmpty()) {
            // 실행 시마다 랜덤하게 섞기 위해 시드 제거
            Collections.shuffle(allQuests);

            if (allQuests.size() > 5) {
                return allQuests.subList(0, 5);
            }
        }
        return allQuests;
    }

    @Transactional
    public void certifyQuest(int userId, int questNo, MultipartFile file) {
        // [수정] 관리자 인증 없이 즉시 포인트 지급 및 완료 처리

        // 1. 퀘스트 정보 조회 (포인트 확인용)
        Quest quest = questMapper.selectQuestByNo(questNo);
        if (quest == null) {
            throw new RuntimeException("존재하지 않는 퀘스트입니다.");
        }

        // 2. 포인트 지급 (POINT_WALLET 업데이트)
        int pointResult = questMapper.updateMemberPoints(userId, quest.getPoint());
        if (pointResult <= 0) {
            throw new RuntimeException("포인트 지급 실패");
        }

        // 3. 퀘스트 수행 내역 저장 (이미지는 저장하지 않음)
        questMapper.insertQuestHistory(userId, questNo, null);
    }
}
