package com.kh.spring.quest.service;

import com.kh.spring.quest.model.vo.Quest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestService {

    private final List<Quest> questList = new ArrayList<>();

    public QuestService() {
        initializeQuests();
    }

    private void initializeQuests() {
        // 집
        questList.add(new Quest(1, "오늘 하루동안 플라스틱, 종이빨대 사용 금지", "집", 10));
        questList.add(new Quest(2, "사용하지 않는 콘센트 뽑기", "집", 10));

        // 회사/학교
        questList.add(new Quest(3, "출퇴근/등하교 시 대중교통 이용하기(자가용 금지)", "회사/학교", 10));
        questList.add(new Quest(4, "종이컵 대신 텀블러 사용하기", "회사/학교", 10));
        questList.add(new Quest(5, "엘리베이터 대신 계단 이용하기", "회사/학교", 10));
    }

    public List<Quest> getDailyQuests() {
        // 현재는 고정된 5개 리스트 반환
        return questList;
    }

    public String certifyQuest(int questNo, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드된 파일이 없습니다.");
        }

        // 저장 경로 설정 (C:/uploadFiles/quest/)
        String uploadDir = "C:/uploadFiles/quest/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        try {
            file.transferTo(new File(uploadDir + savedFilename));
            // TODO: 추후 DB에 인증 내역 저장 및 포인트 지급 로직 연동 필요
            return "업로드 성공: " + savedFilename + " (퀘스트 " + questNo + "번 인증 완료)";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}
