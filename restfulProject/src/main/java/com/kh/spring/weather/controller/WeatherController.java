package com.kh.spring.weather.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.weather.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "날씨 API 조회", description = "날씨 API 조회")
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    // 기본 기상 관측 자료 조회
    @Operation(summary = "기본 기상 관측 자료 조회", description = "기본 기상 관측 자료 조회")
    @GetMapping("/obs")
    public ResponseEntity<?> getObs() throws Exception {
        // API URL을 만듭니다.
        // 서울, 키값 설정 파라미터 고정
        URL url = new URL(
                // https://apihub.kma.go.kr/api/json?authKey=YOUR_AUTH_KEY
                "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.json?stn=108&help=0&authKey=KaG2mDn1S7ihtpg59Su46A");
        // HttpURLConnection 객체를 만들어 API를 호출합니다.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 요청 방식을 GET으로 설정합니다.
        con.setRequestMethod("GET");
        // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
        con.setRequestProperty("Content-Type", "application/json");

        // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        // BufferedReader를 닫습니다.
        in.close();

        System.out.println(response.toString());

        // 응답을 출력합니다.
        return ResponseEntity.ok(response.toString());

    }

    // 황사 자료
    @Operation(summary = "황사 자료 조회", description = "황사 자료 조회")
    @GetMapping("/dust")
    public ResponseEntity<?> getDust() {

    }

    // 자외선 자료
    @Operation(summary = "자외선 자료 조회", description = "자외선 자료 조회")
    @GetMapping("/uv")
    public ResponseEntity<?> getUv() {

    }

}
