package com.kh.spring.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.ecoshop.service.EcoShopService;
import com.kh.spring.ecoshop.vo.EcoShop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeoulMapService {

    private final SeoulMapClient seoulMapClient;
    private final EcoShopService ecoShopService;
    private final ObjectMapper objectMapper;

    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        String combinedThemeIds = String.join(",", themeIds);
        return seoulMapClient.fetchMapData(combinedThemeIds, x, y, distance, keyword);
    }

    public String getDetail(String themeId, String contsId) {
        String detailJson = seoulMapClient.fetchDetail(themeId, contsId);

        try {
            JsonNode root = objectMapper.readTree(detailJson);
            JsonNode item = root.path("body").get(0);

            if (item != null) {
                // 1. JSON 내의 COT_THEME_ID(11103395 등) 추출
                String themeCode = item.path("COT_THEME_ID").asText();

                // 2. DB에서 실제 카테고리 PK(ESC_ID) 조회
                Long realEscId = ecoShopService.findEscId(themeCode);

                if (realEscId != null) {
                    EcoShop ecoShop = EcoShop.builder()
                            .name(item.path("COT_CONTS_NAME").asText())
                            .address(item.path("COT_ADDR_FULL_OLD").asText())
                            .phone(item.path("COT_TEL_NO").asText("정보없음"))
                            .lat(item.path("COT_COORD_Y").asDouble())
                            .lng(item.path("COT_COORD_X").asDouble())
                            .contsId(item.path("COT_CONTS_ID").asText()) // "zerowaste_0050"
                            .escId(realEscId) // 조회된 PK(3) 입력
                            .isActive(1)
                            .build();

                    ecoShopService.insertEcoShop(ecoShop);
                } else {
                    log.warn("매핑된 카테고리 PK를 찾을 수 없음: {}", themeCode);
                }
            }
        } catch (Exception e) {
            log.error("자동 저장 에러: {}", e.getMessage());
        }

        return detailJson;
    }
}