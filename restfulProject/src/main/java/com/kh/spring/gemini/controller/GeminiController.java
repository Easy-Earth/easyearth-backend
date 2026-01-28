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

// 재미나이에게 질문하고 답변 받는 클래스
@RestController
@Tag(name = "재미나이 사용Controller", description = "데이터를 기반으로 재미나이에게 답변하는 API")
@RequestMapping("/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @Operation(summary = "날씨 정보를 기반으로 환경에 대한 조언을 제공하는 API", description = "날씨 정보를 기반으로 환경에 대한 조언을 제공하는 API")
    // 날씨 정보를 받으면 날씨에 대한 짧은 문구 반환
    @PostMapping("/weather")
    public Map<String, String> weather(@RequestBody Map<String, Object> payload) {

        // 키 하드코딩 해둠 - 나중에 유저에게 입력 받을 수도 있기에
        String apiKey = payload.get("apiKey").toString();
        apiKey = "AIzaSyAN6T6db86pCX6ZOln1-sqeQ2sbxPLQS8U";

        String message = payload.get("item").toString();

        String geminiResponse = geminiService.weather(message, apiKey);

        Map<String, String> response = new HashMap<>();
        response.put("message", geminiResponse);

        return response;
    }

}