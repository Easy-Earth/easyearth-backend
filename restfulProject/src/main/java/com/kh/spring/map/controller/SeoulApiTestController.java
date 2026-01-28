package com.kh.spring.map.controller;

import com.kh.spring.map.service.SeoulMapService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seoul")
@RequiredArgsConstructor
@Tag(name = "서울맵 API", description = "서울맵 API 테스트")
public class SeoulApiTestController {
    private final SeoulMapService seoulMapService;

    @Operation(summary = "API 테스트 ", description = "API 테스트")
    @GetMapping("/test")
    public String test(
            @RequestParam List<String> themeIds,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Integer distance
    ) {
        return seoulMapService.getFilteredMapData(themeIds, x, y, distance);
    }
}