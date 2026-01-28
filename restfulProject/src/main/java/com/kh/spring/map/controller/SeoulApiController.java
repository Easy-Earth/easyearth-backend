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

    //126.974695
    //37.564150
    //11103395
    @Operation(summary = "테마 필터링", description = "테마 / 거리 / 키워드를 지정하는 API")
    @GetMapping("/themes/contents")
    public String theme(
            @RequestParam List<String> themeIds,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) String keyword
    ) {
        return seoulMapService.getFilteredMapData(themeIds, x, y, distance, keyword);
    }
}