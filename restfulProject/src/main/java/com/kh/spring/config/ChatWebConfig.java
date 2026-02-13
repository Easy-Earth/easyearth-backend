package com.kh.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ChatWebConfig implements WebMvcConfigurer {
    
    //채팅 전용 WebMvcConfigurer
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 채팅 멀티미디어 파일 접근 (로컬 저장소 연결)
        registry.addResourceHandler("/chat/file/**")
                .addResourceLocations("file:///C:/uploadFiles/chat/");
    }
}
