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

    public String fetchMapData(String combinedIds, Double x, Double y, Integer distance, String keyword) {
        String url = baseUrl + contentsPath.replace("{themeKey}", themeKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page_size", 100)
                .queryParam("page_no", 1)
                .queryParam("coord_x", x)
                .queryParam("coord_y", y)
                .queryParam("search_type", 0)
                .queryParam("search_name", keyword)
                .queryParam("distance", distance)
                .queryParam("theme_id", combinedIds)
                .queryParam("content_id", "")
                .queryParam("subcate_id", 1);

        String finalUrl = builder.build().toUriString();
        System.out.println("전송되는 최종 URL: " + finalUrl);

        return restTemplate.getForObject(finalUrl, String.class);
    }
}