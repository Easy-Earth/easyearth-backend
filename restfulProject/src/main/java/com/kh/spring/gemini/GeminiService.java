package com.kh.spring.gemini;

import org.springframework.stereotype.Service;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Service
public class GeminiService {

    public String weather(String message, String apiKey) {

        Client client = Client.builder().apiKey(apiKey).build();

        String order = "[프롬프트]"
                + "너는 기상 데이터를 분석해 사람들에게 매일 환경을 위한 행동을 독려하는 '환경 비서'야\n"
                + "아래의 기상청 JSON 데이터를 읽고, 오늘의 날씨에 딱 맞는 '발랄하고 귀여운 한 줄 조언'을 작성해줘\n"
                + "[데이터 해석 규칙]"
                + "TMP: 현재 기온 ($^{\\circ}C$)"
                + "SKY: 하늘 상태 (1: 맑음, 3: 구름많음, 4: 흐림)"
                + "POP: 강수 확률 (%)"
                + "[작성 조건]"
                + "아주 발랄하고 에너지가 넘치는 말투(예: ~하자! 등)를 사용할 것"
                + "날씨 정보(기온, 하늘 상태 등)를 자연스럽게 녹여내고 환경에 대한 조언을 포함할 것"
                + "딱 한 줄로만 짧게 쓸 것"
                + "귀여운 이모지를 하나 포함할 것"
                + "입력 데이터:";

        String lastOrder = "";

        GenerateContentResponse response = client.models.generateContent(
                "gemma-3-27b-it",
                order + message + lastOrder,
                null);

        return response.text();
    }

}
