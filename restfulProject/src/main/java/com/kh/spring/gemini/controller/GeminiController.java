package com.kh.spring.gemini.controller;

import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.gemini.service.GeminiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.util.HashMap;

/*
    기상청 단기 예보 샘플
    {
    "apiKey": "AIzaSyAN6T6db86pCX6ZOln1-sqeQ2sbxPLQS8U",
        "item": [
          {
            "baseDate": "20260127",
            "baseTime": "0500",
            "category": "TMP",
            "fcstDate": "20260127",
            "fcstTime": "0600",
            "fcstValue": "-2",
            "nx": 60,
            "ny": 127
          },
          {
            "baseDate": "20260127",
            "baseTime": "0500",
            "category": "SKY",
            "fcstDate": "20260127",
            "fcstTime": "0600",
            "fcstValue": "1",
            "nx": 60,
            "ny": 127
          },
          {
            "baseDate": "20260127",
            "baseTime": "0500",
            "category": "POP",
            "fcstDate": "20260127",
            "fcstTime": "0600",
            "fcstValue": "0",
            "nx": 60,
            "ny": 127
          }
        ]
    }

*/

// 재미나이에게 질문하고 답변 받는 클래스(하드코딩)
@RestController
@Tag(name = "재미나이 기능")
@RequestMapping("/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @Operation(summary = "날씨 정보를 기반으로 gemini에게 질문하고 답변 받기")
    // 날씨 정보를 받으면 날씨에 대한 짧은 문구 반환
    @PostMapping("/weather")
    public Map<String, String> weather(@RequestBody Map<String, Object> payload) {
      
      // 키 하드코딩 해둠 - 나중에 유저에게 입력 받을 수도 있기에
      String apiKey = payload.get("apiKey").toString();
      apiKey = "AIzaSyAN6T6db86pCX6ZOln1-sqeQ2sbxPLQS8U";

      // 날씨 정보를 받으면 날씨에 대한 짧은 문구 반환
      String message = payload.get("item").toString();

      //하드코딩
      message = "오늘은 황사가 매우 심해요";

      String geminiResponse = geminiService.weather(message, apiKey);

      Map<String, String> response = new HashMap<>();
      response.put("message", geminiResponse);

      return response;
    }

    @Autowired
    private com.kh.spring.weather.service.WeatherService weatherService;

    @Operation(summary = "환경 비서에게 오늘의 조언 듣기")
    @PostMapping("/secretary")
    public Map<String, String> getSecretaryAdvice() {
        // 1. 모든 날씨 데이터 수집
        Map<String, Object> weatherData = weatherService.getCheckWeather();

        System.out.println(weatherData);

        // 2. Gemini에게 조언 요청
        String advice = geminiService.generateSecretaryAdvice(weatherData);

        // 3. 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", advice);
        return response;
    }

}