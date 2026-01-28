package com.kh.spring.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeoulMapService {
    private final SeoulMapClient seoulMapClient;

    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        String combinedThemeIds = String.join(",", themeIds);
        return seoulMapClient.fetchMapData(combinedThemeIds, x, y, distance, keyword);
    }

    public String getDetail(String themeId, String contsId) {
        return seoulMapClient.fetchDetail(themeId, contsId);
    }
}