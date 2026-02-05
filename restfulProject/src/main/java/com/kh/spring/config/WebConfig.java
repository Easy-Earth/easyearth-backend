package com.kh.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                                .resourceChain(false);

                registry.addResourceHandler("/webjars/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                                .resourceChain(false);

                registry.addResourceHandler("/**")
                                .addResourceLocations(
                                                "classpath:/META-INF/resources/",
                                                "classpath:/resources/",
                                                "classpath:/static/",
                                                "classpath:/public/");

                // [추가] 업로드 파일 접근 설정 (Mac)
                registry.addResourceHandler("/uploads/chat/**")
                                .addResourceLocations("file:///Users/sunpooh/Projects/easyearth-backend/uploads/chat/");

                registry.addResourceHandler("/uploads/community/**")
                                .addResourceLocations(
                                                "file:///Users/sunpooh/Projects/easyearth-backend/uploads/community/");

                registry.addResourceHandler("/uploads/quest/**")
                                .addResourceLocations(
                                                "file:///Users/sunpooh/Projects/easyearth-backend/uploads/quest/");
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                                .allowedOrigins("http://localhost:5173")
                                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                                .allowedHeaders("*")
                                .allowCredentials(true);
        }

}
