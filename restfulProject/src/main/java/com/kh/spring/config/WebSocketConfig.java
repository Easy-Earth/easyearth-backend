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

    //접속 주소 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    
    //메시지 라우팅
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지 구독 요청(구독 주소) 기능과
        //topic이나 queue로 시작하는 메시지는 구독중인 모든 사람에게 즉시 전달 기능
        registry.enableSimpleBroker("/topic","/queue");

        //메시지 발신 요청(발신 주소)
        //app으로 시작하면 컨트롤러로 보냄(@MessageMapping으로 라우팅)
        registry.setApplicationDestinationPrefixes("/app");
    }
    
}
