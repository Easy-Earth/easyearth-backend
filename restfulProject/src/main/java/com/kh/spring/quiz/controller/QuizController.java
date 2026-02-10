package com.kh.spring.quiz.controller;

import com.kh.spring.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "퀴즈 API", description = "퀴즈 API")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 조회", description = "난이도별 퀴즈를 조회하는 API(Easy,Normal,Hard)")
    @GetMapping("/{difficulty}")
    public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(
            @PathVariable("difficulty") String difficulty) {
        return quizService.getQuizByDifficulty(difficulty);
    }

    @Operation(summary = "퀴즈 결과 저장", description = "퀴즈 완료 후 점수와 이력을 저장합니다.")
    @PostMapping("/result")
    public ResponseEntity<String> saveQuizResult(
            @RequestParam int userId,
            @RequestParam String difficulty,
            @RequestParam int score) {
        try {
            quizService.saveQuizResult(userId, difficulty, score);
            return ResponseEntity.ok("퀴즈 이력이 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("저장 중 오류 발생: " + e.getMessage());
        }
    }
}
