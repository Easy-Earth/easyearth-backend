package com.kh.spring.kakao;

import lombok.Getter;

@Getter
public class KakaoTokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
}

