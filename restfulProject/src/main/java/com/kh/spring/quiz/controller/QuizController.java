package com.kh.spring.quiz.controller;

import com.kh.spring.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Tag(name = "퀴즈 API", description = "퀴즈 API")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 조회", description = "난이도별 퀴즈를 조회하는 API(Easy,Normal,Hard)")
    @org.springframework.web.bind.annotation.GetMapping("/{difficulty}")
    public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(
            @org.springframework.web.bind.annotation.PathVariable("difficulty") String difficulty) {
        return quizService.getQuizByDifficulty(difficulty);
    }
}
