package com.kh.spring.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Tag(name = "채팅", description = "채팅 관련 API")
@Slf4j
public class ChatController {

    @Operation(summary = "채팅 테스트", description = "채팅 테스트 API")

    @GetMapping("/test")
    public ResponseEntity<?> getChatRoomList() {
        log.info("테스트 주소");

        return ResponseEntity.ok("테스트 성공");
    }

}
