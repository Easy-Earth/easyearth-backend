package com.kh.spring.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    /**
     * 테마 필터링 데이터를 조회합니다.
     */
    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        String combinedThemeIds = String.join(",", themeIds);
        return seoulMapClient.fetchMapData(combinedThemeIds, x, y, distance, keyword);
    }

    /**
     * 상세정보 조회 시:
     * 1. 외부 API 데이터 수신
     * 2. DB에 해당 가게 정보 자동 저장 (중복 체크 포함)
     * 3. 우리 DB에서 평균 평점(avgRating)과 리뷰 개수(reviewCount)를 조회하여 JSON에 추가
     */
    public String getDetail(String themeId, String contsId) {
        // 1. 서울시 외부 API로부터 상세 정보 JSON 수신
        String detailJson = seoulMapClient.fetchDetail(themeId, contsId);

        try {
            JsonNode root = objectMapper.readTree(detailJson);
            // 서울시 응답 구조상 "body" 배열의 첫 번째 요소가 실제 데이터
            JsonNode item = root.path("body").get(0);

            if (item != null) {
                // --- [기본 기능] 데이터베이스 저장 로직 ---
                String themeCode = item.path("COT_THEME_ID").asText();

                // 테마 코드로 실제 카테고리 PK 조회
                Long realEscId = ecoShopService.findEscId(themeCode);

                if (realEscId != null) {
                    EcoShop ecoShop = EcoShop.builder()
                            .name(item.path("COT_CONTS_NAME").asText())
                            .address(item.path("COT_ADDR_FULL_OLD").asText())
                            .phone(item.path("COT_TEL_NO").asText("정보없음"))
                            .lat(item.path("COT_COORD_Y").asDouble())
                            .lng(item.path("COT_COORD_X").asDouble())
                            .contsId(item.path("COT_CONTS_ID").asText())
                            .escId(realEscId)
                            .isActive(1)
                            .build();

                    // 서비스 내부에서 중복 체크 후 INSERT 수행
                    ecoShopService.insertEcoShop(ecoShop);
                }

                // --- [신규 기능] 평균 평점 및 리뷰 개수 데이터 결합 ---
                // 2. 우리 DB에서 통계 데이터 조회
                double avgRating = ecoShopService.getAverageRating(contsId);
                int reviewCount = ecoShopService.getReviewCount(contsId);

                // 3. Jackson ObjectNode를 사용하여 응답 JSON에 필드 추가
                if (item instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) item;
                    objectNode.put("avgRating", avgRating);
                    objectNode.put("reviewCount", reviewCount);
                }

                // 수정된 데이터가 포함된 전체 JSON 구조를 문자열로 반환
                return root.toString();
            }
        } catch (Exception e) {
            log.error("상세 정보 처리 중 에러 발생: {}", e.getMessage());
        }

        return detailJson; // 에러 발생 시 원본 데이터 반환
    }
}