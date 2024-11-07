package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/** 경매후 수선가격 푸쉬알림을 위한 SSE 통신 컨트롤러 */
@RestController
@Slf4j
public class AppNotificationController {
    private SseEmitter emitter;

    @GetMapping(value = "/auction_listener", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private SseEmitter appNotification() {
        emitter = new SseEmitter();
        return emitter;
    }

    public void sendNotification(String message) {
        try {
            emitter.send(message, MediaType.TEXT_PLAIN);
            emitter.complete();
        } catch (Exception e) {
            log.error("알림 보내기 실패");
        }
    }
}