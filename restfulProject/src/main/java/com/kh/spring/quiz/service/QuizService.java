package com.kh.spring.quiz.service;

import com.kh.spring.quiz.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {

        private final QuizMapper quizMapper;

        public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(String difficulty) {
                return quizMapper.selectQuizByDifficulty(difficulty);
        }
}
