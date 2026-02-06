package com.kh.spring.quiz.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuizMapper {
    java.util.List<com.kh.spring.quiz.model.vo.Quiz> selectQuizListByDifficulty(String difficulty);

    int countTodayAttempts(@org.apache.ibatis.annotations.Param("userId") int userId,
            @org.apache.ibatis.annotations.Param("difficulty") String difficulty);

    int insertQuizHistory(@org.apache.ibatis.annotations.Param("userId") int userId,
            @org.apache.ibatis.annotations.Param("difficulty") String difficulty,
            @org.apache.ibatis.annotations.Param("score") int score);
}
