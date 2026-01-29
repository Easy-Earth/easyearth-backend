package com.kh.spring.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.weather.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "날씨 API 조회")
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    //일기예보 조회
    @Operation(summary = "일기예보 조회")
    @GetMapping("/forecast")
    public ResponseEntity<?> getForecast(){

        return ResponseEntity.ok(weatherService.getForecastList());

    }

    // 기본 기상 관측 자료 조회
    @Operation(summary = "기본 기상 관측 자료 조회")
    @GetMapping("/obs")
    public ResponseEntity<?> getObs(){

        return ResponseEntity.ok(weatherService.getObsList());

    }

    // 황사+ 미세먼지
    @Operation(summary = "황사 자료 조회")
    @GetMapping("/dust")
    public ResponseEntity<?> getDust() {

        return ResponseEntity.ok(weatherService.getDustList());

    }

    // 자외선 자료
    @Operation(summary = "자외선 자료 조회")
    @GetMapping("/uv")
    public ResponseEntity<?> getUv() {

        return ResponseEntity.ok(weatherService.getUvList());

    }

}
