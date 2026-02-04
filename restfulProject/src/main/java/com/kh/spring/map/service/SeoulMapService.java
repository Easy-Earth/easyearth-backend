package com.kh.spring.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kh.spring.ecoshop.service.EcoShopService;
import com.kh.spring.ecoshop.vo.EcoShop;
import com.kh.spring.ecoshop.vo.Review;
import com.kh.spring.ecoshop.vo.ReviewerName;
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
        String detailJson = seoulMapClient.fetchDetail(themeId, contsId);

        try {
            JsonNode root = objectMapper.readTree(detailJson);
            JsonNode item = root.path("body").get(0);

            if (item != null && item instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) item;

                // 1. DB에 상점 정보 저장/업데이트 후 숫자 PK(shopId) 가져오기
                // insertEcoShop 로직을 거친 후, contsId로 DB의 숫자 ID를 조회합니다.
                // (기존 insert 로직은 그대로 유지)

                // 2. DB의 숫자 ID 조회 (Review 조회를 위해 필수)
                int realShopId = ecoShopService.findShopIdByContsId(contsId);

                if (realShopId > 0) {
                    // 3. 숫자 ID로 리뷰 리스트 조회
                    List<ReviewerName> reviews = ecoShopService.reviewList(realShopId);

                    // 4. 리뷰 리스트를 JSON 배열로 변환하여 추가
                    JsonNode reviewsNode = objectMapper.valueToTree(reviews);
                    objectNode.set("reviews", reviewsNode);

                    // 5. 통계 데이터 추가 (기존 로직)
                    double avgRating = ecoShopService.getAverageRating(contsId);
                    int reviewCount = ecoShopService.getReviewCount(contsId);
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