package com.kh.spring.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
     * 수정: 서울시 API의 다중 테마 요청 제약을 우회하기 위해
     * 각 테마 ID별로 개별 호출 후 결과를 병합합니다.
     */
    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        if (themeIds == null || themeIds.isEmpty()) {
            return "{\"body\": []}";
        }

        try {
            // 모든 결과를 담을 통합 배열 생성
            ArrayNode combinedArray = objectMapper.createArrayNode();
            ObjectNode rootNode = objectMapper.createObjectNode();

            for (String themeId : themeIds) {
                try {
                    // 1. 각 테마 ID별로 독립적인 API 호출 (400 에러 방지)
                    String jsonResponse = seoulMapClient.fetchMapData(themeId, x, y, distance, keyword);

                    // 2. 응답 데이터 파싱 및 body 추출
                    JsonNode responseRoot = objectMapper.readTree(jsonResponse);
                    JsonNode bodyNode = responseRoot.path("body");

                    // 3. body 내의 아이템들을 통합 배열에 추가
                    if (bodyNode.isArray()) {
                        for (JsonNode item : bodyNode) {
                            combinedArray.add(item);
                        }
                    }
                } catch (Exception e) {
                    log.error("테마 ID [{}] 조회 중 오류 발생: {}", themeId, e.getMessage());
                    // 특정 테마 호출 실패 시에도 다른 테마 조회를 위해 루프 계속 진행
                }
            }

            // 4. 최종 합쳐진 데이터를 {"body": [...]} 구조로 반환
            rootNode.set("body", combinedArray);
            return objectMapper.writeValueAsString(rootNode);

        } catch (Exception e) {
            log.error("데이터 병합 처리 중 치명적 오류 발생: {}", e.getMessage());
            return "{\"body\": []}";
        }
    }

    /**
     * 상세정보 조회 (기존 로직 유지)
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
                double avgRating = ecoShopService.getAverageRating(contsId);
                int reviewCount = ecoShopService.getReviewCount(contsId);

                // Jackson ObjectNode를 사용하여 응답 JSON에 필드 추가
                if (item instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) item;
                    objectNode.put("avgRating", avgRating);
                    objectNode.put("reviewCount", reviewCount);
                }

                return root.toString();
            }
        } catch (Exception e) {
            log.error("상세 정보 처리 중 에러 발생: {}", e.getMessage());
        }

        return detailJson;
    }
}