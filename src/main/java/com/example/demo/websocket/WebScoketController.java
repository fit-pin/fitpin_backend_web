package com.example.demo.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebScoketController {

	private final SimpMessagingTemplate messagingTemplate;

	// 클라이언트 메시지가 전송되는 곳
	// 이때 경로는 setApplicationDestinationPrefixes+설정값 즉 /recv/
	@MessageMapping("/")
	private void clinetResponse(RecvDTO message) throws Exception {
		log.info("클라이언트 접속: " + message.getToken());
	}

	/**
	 * 웹소켓에 데이터를 전송합니다
	 * 
	 * @param sendData 전송할 데이터
	 */
	public void sendClient(ActionDTO sendData) {
		messagingTemplate.convertAndSend("/action", sendData);
	}
}