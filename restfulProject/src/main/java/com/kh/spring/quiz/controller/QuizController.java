package com.kh.spring.quiz.controller;

import com.kh.spring.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @org.springframework.web.bind.annotation.GetMapping("/{difficulty}")
    public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(
            @org.springframework.web.bind.annotation.PathVariable("difficulty") String difficulty) {
        return quizService.getQuizByDifficulty(difficulty);
    }
}
