package com.example.demo.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketServer extends TextWebSocketHandler {
    
    public Map<String, WebSocketSession> client = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getId() +": 세션 연결됨");
        client.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(session.getId() +": 세션 종료됨");
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(session.getId()+": "+ message.getPayload());
        client.forEach((k, v) -> {
            try {
                v.sendMessage(message);
            } catch (Exception e) {
                System.out.println("메시지 전송 실패");
            }
        });
    }
}