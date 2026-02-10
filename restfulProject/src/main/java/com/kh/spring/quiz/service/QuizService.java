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

                // 3. 10문제만 추출 (또는 전체 반환 원하시면 subList 제거 가능)
                // 여기서는 매번 새로운 10문제를 제공하기 위해 상위 10개만 리턴합니다.
                // 만약 50문제 전체를 원하시면 이 부분을 filteredList 그대로 리턴하면 됩니다.
                int limit = Math.min(filteredList.size(), 10);
                return filteredList.subList(0, limit);
        }
}
