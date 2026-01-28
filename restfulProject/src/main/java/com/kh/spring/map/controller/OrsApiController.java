package com.kh.spring.map.controller;

import com.kh.spring.map.service.OrsRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name="Ors Api", description = "Ors Api")
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class OrsApiController {

    private final OrsRouteService orsRouteService;

    @Operation(summary = "길찾기 Api", description = "자동차 : driving-car / 도보 : foot-walking / 자전거 : cycling-regular")
    @GetMapping("/ors")
    public ResponseEntity<?> getOrsRoute(
            @RequestParam(defaultValue = "126.974695") Double startX,
            @RequestParam(defaultValue = "37.564149") Double startY,
            @RequestParam(defaultValue ="127.106928082") Double goalX,
            @RequestParam(defaultValue = "37.580981471") Double goalY,
            @RequestParam(defaultValue = "driving-car") String mode) {

        Map<String, Object> result = orsRouteService.getRouteWithEcoInfo(startX, startY, goalX, goalY, mode);

        if (result.containsKey("error")) {
            return ResponseEntity.internalServerError().body(result.get("error"));
        }

        return ResponseEntity.ok(result);
    }
}
