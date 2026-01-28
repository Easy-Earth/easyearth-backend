package com.kh.spring.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoulMapService {
    private final SeoulMapClient seoulMapClient;

    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        // 명세서에 따라 "여러 테마는 콤마로 구분"하여 전달
        String combinedThemeIds = String.join(",", themeIds);
        return seoulMapClient.fetchMapData(combinedThemeIds, x, y, distance, keyword);
    }
}