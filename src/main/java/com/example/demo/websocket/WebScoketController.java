package com.example.demo.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebScoketController {

	// 클라이언트 메시지가 전송되는 곳 
	// 이때 경로는 setApplicationDestinationPrefixes+설정값 즉 /recv/
	@MessageMapping("/")
	@SendTo("/action") //  클라이언트에게 메시지를 보내 곳
	public ActionDTO ClinetResponse(RecvDTO message) throws Exception {
		System.out.println(message.getKey());
		return new ActionDTO("진짜 되는거 실화냐 ㄹㅇ 가슴이 웅장해진다");
	}
}
