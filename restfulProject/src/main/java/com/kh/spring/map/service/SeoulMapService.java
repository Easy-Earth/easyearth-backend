package com.kh.spring.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kh.spring.ecoshop.service.EcoShopService;
import com.kh.spring.ecoshop.vo.EcoShop;
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

    public String getFilteredMapData(List<String> themeIds, Double x, Double y, Integer distance, String keyword) {
        if (themeIds == null || themeIds.isEmpty()) {
            return "{\"body\": []}";
        }

        try {
            ArrayNode combinedArray = objectMapper.createArrayNode();
            ObjectNode rootNode = objectMapper.createObjectNode();

            for (String themeId : themeIds) {
                try {
                    String jsonResponse = seoulMapClient.fetchMapData(themeId, x, y, distance, keyword);
                    JsonNode responseRoot = objectMapper.readTree(jsonResponse);
                    JsonNode bodyNode = responseRoot.path("body");

                    if (bodyNode.isArray()) {
                        for (JsonNode item : bodyNode) {
                            combinedArray.add(item);
                        }
                    }
                } catch (Exception e) {
                    log.error("테마 ID [{}] 조회 중 오류 발생: {}", themeId, e.getMessage());
                }
            }
            rootNode.set("body", combinedArray);
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            log.error("데이터 병합 처리 중 치명적 오류 발생: {}", e.getMessage());
            return "{\"body\": []}";
        }
    }

    public String getDetail(String themeId, String contsId) {
        String detailJson = seoulMapClient.fetchDetail(themeId, contsId);

        try {
            JsonNode root = objectMapper.readTree(detailJson);
            JsonNode itemNode = root.path("body").get(0);

            if (itemNode != null && itemNode instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) itemNode;

                Integer shopIdResult = ecoShopService.findShopIdByContsId(contsId);
                int realShopId = 0;

                if (shopIdResult != null && shopIdResult > 0) {
                    realShopId = shopIdResult;
                } else {
                    // 💡 [카테고리 판별 로직] themeId에 따라 ESC_ID를 결정합니다.
                    long escId = 0L;
                    if (themeId != null) {
                        if (themeId.contains("eco_tumbler")) escId = 2L;
                        else if (themeId.contains("zerowaste")) escId = 3L;
                        else if (themeId.contains("wm")) escId = 5L;
                        else escId = 1L; // 기본값
                    }

                    log.info("신규 상점 등록 (카테고리 {}): {}", escId, contsId);

                    EcoShop newShop = EcoShop.builder()
                            .name(itemNode.path("COT_CONTS_NAME").asText())
                            .address(itemNode.path("COT_ADDR_FULL_NEW").asText())
                            .phone(itemNode.path("COT_TEL_NO").asText())
                            .lat(itemNode.path("COT_COORD_Y").asDouble())
                            .lng(itemNode.path("COT_COORD_X").asDouble())
                            .contsId(contsId)
                            .escId(escId)
                            .build();

                    ecoShopService.insertEcoShop(newShop);
                    realShopId = newShop.getShopId();
                }

                objectNode.put("shopId", realShopId);

                if (realShopId > 0) {
                    List<ReviewerName> reviews = ecoShopService.reviewList(realShopId);
                    objectNode.set("reviews", objectMapper.valueToTree(reviews));

                    objectNode.put("avgRating", ecoShopService.getAverageRating(contsId));
                    objectNode.put("reviewCount", ecoShopService.getReviewCount(contsId));
                }

                return root.toString();
            }
        } catch (Exception e) {
            log.error("상세 정보 처리 중 에러 발생: ", e);
        }
        return detailJson;
    }
}