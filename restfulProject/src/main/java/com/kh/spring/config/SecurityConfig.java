package com.kh.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;

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
						.requestMatchers("/items/**").permitAll() // 아이템 관련 경로는 일단 다 허용
						.requestMatchers("/ws-chat/**").permitAll() // 웹소켓 허용

						// [Quest Controller] 퀘스트 인증(포인트 지급)은 로그인 필수
						.requestMatchers("/api/quest/certify/**").authenticated()

						// [Quiz Controller] 퀴즈 결과 저장(포인트 지급)은 로그인 필수
						.requestMatchers("/api/quiz/attempt").authenticated()

						// [Attendance Controller] 출석 체크 및 조회는 로그인 필수 (Controller
						// @RequestMapping("/attendance") 기준)
						.requestMatchers("/attendance/**").authenticated()

						.anyRequest().permitAll() // 그 외 모든 요청도 일단 허용 (테스트용)
				)
				// JWT 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 실행)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}