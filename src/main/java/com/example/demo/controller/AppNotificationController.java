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
        log.info(appUserEmail + ": SSE 새션 연결");
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> {
            log.info(appUserEmail + ": SSE 세션 연결 끊김");
            emitterMap.remove(appUserEmail);
        });
        emitter.onError((e) -> {
            log.info(appUserEmail + ": SSE 세션 연결 오류: " + e.getMessage());
            emitterMap.remove(appUserEmail);
        });
        emitterMap.put(appUserEmail, emitter);
        return emitter;
    }

    public void sendNotification(Object message, String appUserEmail) {
        new Thread(() -> {
            try {
                SseEmitter emitter = emitterMap.get(appUserEmail);

                // RN SSE 클라이언트에 지속적 끊김 현상으로 인해
                // 0.5 초 간격으로 10동안 지속적으로 재연결 되기를 기다림
                for (int i = 0; i < 20; i++) {
                    if (emitter != null) {
                        break;
                    }
                    Thread.sleep(500);
                    emitter = emitterMap.get(appUserEmail);
                    log.info(appUserEmail + " 에게 " + (i + 1) + "번째 푸쉬 메시지 보내기 재시도");
                }
                emitter.send(message, MediaType.APPLICATION_JSON);
                emitter.complete();
            } catch (Exception e) {
                log.error("알림 보내기 실패", e);
            }
        }).start();

    }
}