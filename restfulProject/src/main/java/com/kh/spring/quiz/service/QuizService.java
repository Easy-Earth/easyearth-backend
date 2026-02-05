package com.kh.spring.quiz.service;

import com.kh.spring.quiz.mapper.QuizMapper;
import com.kh.spring.quiz.model.vo.Quiz;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

        private final QuizMapper quizMapper;

        /**
         * 난이도별 퀴즈 목록을 DB에서 조회한 후 랜덤으로 5문항을 반환합니다.
         * 하루에 한 번만 갱신되는 로직을 위해 날짜 기반 시드(Seed)를 사용합니다.
         */
        public List<Quiz> getQuizByDifficulty(String difficulty) {

                // [테스트 시 주석 해제하여 사용 - 하루 1회 제한 로직]
                /*
                 * int userId = 1; // 실제 구현 시 세션 유저 사용
                 * int todayAttempts = quizMapper.countTodayAttempts(userId, difficulty);
                 * if (todayAttempts > 0) {
                 * throw new RuntimeException("오늘 해당 난이도의 퀴즈를 이미 수행하셨습니다.");
                 * }
                 */

                List<Quiz> allQuizzes = quizMapper.selectQuizListByDifficulty(difficulty);

                if (allQuizzes == null || allQuizzes.isEmpty()) {
                        return Collections.emptyList();
                }

                // 실행 시마다 랜덤하게 섞기 위해 시드 제거
                Collections.shuffle(allQuizzes);

                return allQuizzes.stream()
                                .limit(5)
                                .collect(Collectors.toList());
        }

        /**
         * 퀴즈 결과를 저장합니다.
         */
        public void saveQuizResult(int userId, String difficulty, int score) {
                quizMapper.insertQuizHistory(userId, difficulty, score);
        }
}
