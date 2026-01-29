package com.kh.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//채팅을 위한 웹소켓 설정입니다.

@Configuration
@EnableWebSocketMessageBroker //STOMP 사용 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //클라이언트(리액트)가 처음 연결할 주소 : ws://localhost:8080/ws-chat
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지 구독 요청(구독 주소)
        registry.enableSimpleBroker("/topic","/queue");
        //메시지 발신 요청(발신 주소)
        //app으로 시작하면 컨트롤러(@MessageMapping으로 라우팅)
        registry.setApplicationDestinationPrefixes("/app");
    }
    
}
