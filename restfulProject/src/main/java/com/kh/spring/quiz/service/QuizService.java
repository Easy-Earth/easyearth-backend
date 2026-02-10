package com.kh.spring.quiz.service;

import com.kh.spring.quiz.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

        private final QuizMapper quizMapper;
        private final java.util.List<com.kh.spring.quiz.model.vo.Quiz> quizList = new java.util.ArrayList<>();

        public QuizService(QuizMapper quizMapper) {
                this.quizMapper = quizMapper;
                // QuizPool에서 150문제 로드
                this.quizList.addAll(QuizPool.getAllQuizzes());
        }

        public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(String difficulty) {
                java.util.List<com.kh.spring.quiz.model.vo.Quiz> filteredList = new java.util.ArrayList<>();

                // 1. 난이도별 필터링
                for (com.kh.spring.quiz.model.vo.Quiz q : quizList) {
                        if (q.getDifficulty().equalsIgnoreCase(difficulty)) {
                                filteredList.add(q);
                        }
                }

                // 2. 랜덤 섞기 (Shuffle)
                java.util.Collections.shuffle(filteredList);

                // 3. 5문제만 추출 (사용자 요청 사항: 5문제씩 제공)
                int limit = Math.min(filteredList.size(), 5);
                return filteredList.subList(0, limit);
        }
}
