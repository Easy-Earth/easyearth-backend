package com.kh.spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    //http://localhost:8080/spring/swagger-ui/index.html
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyEarth Project API")
                        .description("EasyEarth Project API 문서")
                        .version("v1.0.0"));
    }
}
