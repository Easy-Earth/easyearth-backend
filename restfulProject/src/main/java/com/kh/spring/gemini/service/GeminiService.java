package com.kh.spring.gemini.service;

import org.springframework.stereotype.Service;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import java.util.Map;

@Service
public class GeminiService {

    public String weather(String message, String apiKey) {
        //더미
        return ""; 
    }

    public String generateSecretaryAdvice(Map<String, Object> weatherData) {
        // 프롬프트 구성
        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 사용자에게 친근하고 발랄한 '환경 비서'야. 기상 데이터를 분석해서 오늘 지구가 덜 아프게, 그리고 사용자도 건강하게 보낼 수 있는 꿀팁을 줘야 해.\n\n");
        prompt.append("[오늘의 기상 리포트]\n");
        prompt.append(weatherData.toString() + "\n\n"); // Map toString 자동 변환 이용
        
        prompt.append("[요청 사항]\n");
        prompt.append("1. **오늘의 날씨 브리핑**: 현재 날씨 상태, 기온, 미세먼지 등을 딱 한 문장으로 간단히 요약해줘.\n");
        prompt.append("2. **환경 실천 행동**: 위 날씨에 맞춰서 사용자가 오늘 실천할 수 있는 구체적인 환경 보호 활동을 1~2가지 제안해줘. (예: 햇살이 좋으니 건조기 대신 빨래 널기, 미세먼지 심하니 자가용 대신 대중교통 등)\n");
        prompt.append("3. **응원 메시지**: 하루를 기분 좋게 시작할 수 있는 발랄한 응원 멘트로 마무리해줘.\n");
        prompt.append("4. **톤앤매너**: 이모지를 적절히 섞어서 아주 귀엽고 에너지 넘치게 작성해줘.\n");
        prompt.append("5. 전체 길이는 3~4문장 내외로 너무 길지 않게 해줘.\n");

        // API 호출
        String apiKey = "AIzaSyAN6T6db86pCX6ZOln1-sqeQ2sbxPLQS8U"; 
        Client client = Client.builder().apiKey(apiKey).build();

        try {
            GenerateContentResponse response = client.models.generateContent(
                    "gemma-3-27b-it",
                    prompt.toString(),
                    null);
            return response.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "죄송해요, 환경 비서가 잠시 휴식 중이에요! 😢 날씨 정보를 다시 확인해 주세요.";
        }
    }

}
