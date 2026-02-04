package com.kh.spring.quiz.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuizMapper {
    java.util.List<com.kh.spring.quiz.model.vo.Quiz> selectQuizByDifficulty(String difficulty);
}
