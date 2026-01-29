package com.kh.spring.quest.controller;

import com.kh.spring.quest.model.vo.Quest;
import com.kh.spring.quest.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
@Tag(name = "퀘스트 API", description = "데일리 퀘스트 및 인증 API")
public class QuestController {

    private final QuestService questService;

    @Operation(summary = "오늘의 퀘스트 조회", description = "매일 수행할 5개의 퀘스트 목록을 반환합니다. (파라미터 없음)")
    @GetMapping("/daily")
    public List<Quest> getDailyQuests() {
        return questService.getDailyQuests();
    }

    @Operation(summary = "퀘스트 인증(사진 업로드)", description = "퀘스트 수행 인증 사진을 업로드합니다. (예: questNo=1, file=인증사진.jpg)")
    @PostMapping("/certify/{questNo}")
    public String certifyQuest(
            @PathVariable int questNo,
            @RequestParam("file") MultipartFile file) {
        return questService.certifyQuest(questNo, file);
    }
}
