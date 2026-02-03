package com.kh.spring.quest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    public List<Quest> getDailyQuests() {
        return questMapper.selectDailyQuests();
    }

    @Transactional
    public void certifyQuest(int userId, int questNo, MultipartFile file) {

        // 파일 저장 로직 (간단하게 구현)
        String uploadDir = "D:/space/EasyEarth/uploads/quest/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID() + "_" + originalFilename;
        File dest = new File(uploadDir + savedFileName);

        try {
            file.transferTo(dest);
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        // DB 저장
        questMapper.insertQuestHistory(userId, questNo, savedFileName);
    }
}
