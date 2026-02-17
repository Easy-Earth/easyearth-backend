package com.kh.spring.search.model.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.url.search.news}")
    private String newsSearchUrl;

    public String searchNews(String query, int display, int start, String sort) {
        
        String finalQuery = query;

        // [수정 v4] 검색어 대폭 추가 & 필터 완화
        // "환경" 검색 시 -> 더 넓은 범위의 환경 키워드를 포함하되, 주식/정치 용어는 계속 제외합니다.
        if (query.equals("환경") || query.equals("뉴스") || query.equals("속보")) {
            // 1. 긍정 키워드 (대폭 추가)
            String positiveConfig = 
                  "\"환경\" | \"기후변화\" | \"탄소중립\" | \"재활용\" | \"쓰레기\" | \"플라스틱\" | "
                + "\"미세먼지\" | \"친환경\" | \"생태계\" | \"지속가능\" | \"동물보호\" | \"비건\" | "
                + "\"제로웨이스트\" | \"업사이클링\" | \"플로깅\" | \"분리수거\" | \"자원순환\" | "
                + "\"멸종위기\" | \"해양오염\" | \"대기오염\" | \"지구온난화\" | \"온실가스\"";

            // 2. 부정 키워드 (주식/정치 용어 위주로 컴팩트하게)
            String negativeConfig = "-주식 -증시 -코스피 -코스닥 -특징주 -순매수 -순매도 -상한가 -정치 -선거 -당선 -지지율";

            finalQuery = "(" + positiveConfig + ") " + negativeConfig;
        } 
        else if (query.contains("환경")) {
             finalQuery = query + " -주식 -증시 -코스피 -코스닥 -특징주 -순매수 -순매도 -선거 -정치";
        }
        
        log.info("🔍 최종 검색어 (v4): {}", finalQuery);

        // 1. 요청 URL 만들기 (쿼리 파라미터 추가)
        URI uri = UriComponentsBuilder
                .fromUriString(newsSearchUrl)
                .queryParam("query", finalQuery)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        log.info("Naver API Request URL: {}", uri);

        // 2. 헤더에 Client ID, Secret 추가
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        // 3. API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        
        log.info("Naver API Response Code: {}", result.getStatusCode());

        return result.getBody();
    }
}
