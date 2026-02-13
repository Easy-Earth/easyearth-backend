package com.kh.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 시큐리티 설정을 활성화
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder bcrypt() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // CSRF 방어 비활성화 (API 서버에서는 보통 끔)
				.cors(cors -> cors.disable()) // CORS 설정 (필요시 별도 설정)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/spring/items/**").permitAll() // 아이템 관련 경로는 일단 다 허용
						.anyRequest().permitAll() // 그 외 모든 요청도 일단 허용 (테스트용)
				);

		return http.build();
	}
}