package com.kh.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//채팅을 위한 웹소켓 설정
@Configuration
@EnableWebSocketMessageBroker //STOMP 사용 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //[연결 주소] 터널 입구를 "/ws-chat"로 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    
    //메시지 라우팅
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //[구독 주소 규칙] topic이나 queue로 시작하는 메시지는 브로커가 처리
        registry.enableSimpleBroker("/topic","/queue");

        //[전송 주소 규칙] app으로 시작하면 컨트롤러로 보냄(@MessageMapping으로 라우팅)
        registry.setApplicationDestinationPrefixes("/app");
    }
    
}
