package com.kh.spring.map.controller;

import com.kh.spring.map.service.SeoulMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seoul")
@RequiredArgsConstructor
@Tag(name = "서울맵 API", description = "서울맵 API")
public class SeoulApiController {

    private final SeoulMapService seoulMapService;


    @Operation(summary = "테마 필터링"
              ,description = "테마(1657588761062 ,1693986134109, 11103395, 11101339, 100578, 1765960271640,1730359504536)  / 거리(m) / 키워드를 지정하는 API (126.974695  37.564150)")


    @GetMapping("/themes/contents")
    public String theme(
            @RequestParam List<String> themeIds,
            @RequestParam(required = false, defaultValue = "126.974695") Double x,
            @RequestParam(required = false, defaultValue = "37.564150") Double y,
            @RequestParam(required = false, defaultValue = "2000") Integer distance,
            @RequestParam(required = false) String keyword
    ) {
        return seoulMapService.getFilteredMapData(themeIds, x, y, distance, keyword);
    }

    @Operation(summary = "상세정보 조회", description = "가게 상세정보 조회하는 API zerowaste_0054")
    @GetMapping("/detail")
    public String detail(
            @RequestParam(defaultValue = "11103395") String themeId,
            @RequestParam(defaultValue = "zerowaste_0054") String contsId
    ) {
        return seoulMapService.getDetail(themeId, contsId);
    }
}
