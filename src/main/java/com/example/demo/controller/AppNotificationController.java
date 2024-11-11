package com.example.demo.controller;

import java.util.HashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/** 경매 후 수선가격 푸쉬알림을 위한 SSE 통신 컨트롤러 */
@RestController
@Slf4j
public class AppNotificationController {
    private HashMap<String, SseEmitter> emitterMap = new HashMap<>();

    @GetMapping(value = "/auction_listener/{appUserEmail}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private SseEmitter appNotification(@PathVariable(required = true) String appUserEmail) {
        log.info(appUserEmail+ ": SSE 새션 연결");
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> {
            log.info(appUserEmail+": SSE 세션 연결 끊김");
            emitterMap.remove(appUserEmail);
        });
        emitter.onError((e) -> {
            log.info(appUserEmail+": SSE 세션 연결 오류: "+ e.getMessage());
            emitterMap.remove(appUserEmail);
        });
        emitterMap.put(appUserEmail, emitter);
        return emitter;
    }

    public void sendNotification(Object message, String appUserEmail) {
        try {
            SseEmitter emitter = emitterMap.get(appUserEmail);
            emitter.send(message, MediaType.APPLICATION_JSON);
            emitter.complete();
        } catch (Exception e) {
            log.error("알림 보내기 실패");
        }
    }
}