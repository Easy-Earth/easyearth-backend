package com.kh.spring.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class SeoulMapClient {
    private final RestTemplate restTemplate;

    @Value("${seoulmap.base-url}")
    private String baseUrl;
    @Value("${seoulmap.theme-key}")
    private String themeKey;
    @Value("${seoulmap.contents-path}")
    private String contentsPath;

    public String fetchMapData(String combinedIds, Double x, Double y, Integer distance) {
        String url = baseUrl + contentsPath.replace("{themeKey}", themeKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page_size", 100)      // 명세서 요청항목
                .queryParam("page_no", 1)          // 명세서 요청항목
                .queryParam("theme_id", combinedIds) // 여러 테마는 콤마로 구분
                .queryParam("search_type", 0)       // 0:거리 검색
                .queryParam("coord_x", x)           // 명세서 규격 X좌표
                .queryParam("coord_y", y)           // 명세서 규격 Y좌표
                .queryParam("distance", distance);  // 반경 검색 (기본 2000)

        // 명세서 샘플에 있는 불필요한 파라미터도 빈 값으로 추가하여 서버 거부 방지
        builder.queryParam("search_name", "")
                .queryParam("content_id", "")
                .queryParam("subcate_id", "");

        String finalUrl = builder.build().toUriString();
        System.out.println("전송되는 최종 URL: " + finalUrl);

        return restTemplate.getForObject(finalUrl, String.class);
    }
}