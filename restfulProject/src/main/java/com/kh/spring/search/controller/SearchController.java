package com.kh.spring.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.search.model.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
@Tag(name = "검색", description = "네이버 검색 API")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Operation(summary = "네이버 뉴스 검색", description = "네이버 오픈 API를 이용하여 뉴스를 검색합니다.")
    @GetMapping(value = "/news", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> searchNews(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int display,
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "sim") String sort
    ) {
        log.info("뉴스 검색 요청: query={}, display={}, start={}, sort={}", query, display, start, sort);
        
        String result = searchService.searchNews(query, display, start, sort);
        
        return ResponseEntity.ok(result);
    }
}
