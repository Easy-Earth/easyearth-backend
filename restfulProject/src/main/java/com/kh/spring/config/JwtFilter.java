package com.kh.spring.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kh.spring.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtUtil.validateToken(token)) {

                String loginId = jwtUtil.getUserIdFromToken(token);

                if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    Authentication auth = new UsernamePasswordAuthenticationToken(loginId, null,
                            Collections.emptyList());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT Auth Success: {}", loginId);
                }
            } else {
                log.warn("Invalid JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
