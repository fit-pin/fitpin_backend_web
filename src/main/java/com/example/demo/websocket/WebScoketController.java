package com.example.demo.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.AppNotificationController;
import com.example.demo.websocket.ActionDTO.ItemList;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebScoketController {
	private final SimpMessagingTemplate messagingTemplate;
	private final HashMap<Integer, AuctionBroadcastService> auctionRoom = new HashMap<>();

	private static int conut;

	@Autowired
	AppNotificationController appNotificationController;

	// 클라이언트 메시지가 전송되는 곳
	// 이때 경로는 setApplicationDestinationPrefixes+설정값 즉 /recv/
	@MessageMapping("/Auction/{auctionId}/connect/{token}")
	private void AuctionConnect(@DestinationVariable int auctionId, @DestinationVariable String token) {
		log.info("경매 접속: " + auctionId);

		AuctionBroadcastService room = auctionRoom.get(auctionId);
		if (room != null) {
			if (room.getAuctionId() == auctionId) {
				if (room.isActivete()) {
					room.sendAuctionData(token);
				} else {
					room.createAuction(id -> {
						log.info(id + ":  경매 종료");
						// 경매 종료 시 Map 에서 지우서 GC가 메모리 할당을 해제 하도록 함
						auctionRoom.remove(id);
					}, appNotificationController);
				}
			}
		} else {
			log.info(auctionId + ": 방이 존재하지 않음");
		}
	}

	@MessageMapping("/Auction/{auctionId}/price")
	private void AuctionPrice(@DestinationVariable int auctionId, RecvPrice body) {
		AuctionBroadcastService room = auctionRoom.get(auctionId);
		if (room != null) {
			log.info(auctionId + " 에서 제시된 호가:" + body.getPrice());
			room.sendPrice(body);
		}
	}

	/**
	 * 고객이 의류를 구매한 경우 구독한 모든 클라이언트에게 전달
	 * 
	 * @param sendData 전송할 데이터
	 */
	public void sendBuyItem(ActionDTO sendData) {

		Stream<ItemList> stream = sendData.getItems().stream();

		List<ActionDTOMappper> listMap = stream.filter((item) -> item.pitStatus)
				.map((item) -> {
					ActionDTOMappper actionDTO = new ActionDTOMappper();

					actionDTO.setAuctionId(conut);
					actionDTO.setUserEmail(sendData.getUserEmail());
					actionDTO.setUserName(sendData.getUserName());
					actionDTO.setUserAddr(sendData.getUserAddr());
					actionDTO.setUserNumber(sendData.getUserNumber());
					actionDTO.setItemTotal(sendData.getItemTotal());
					actionDTO.setUserAddrDetail(sendData.getUserAddrDetail());

					actionDTO.setItemKey(item.itemKey);
					actionDTO.setItemName(item.itemName);
					actionDTO.setItemSize(item.itemSize);
					actionDTO.setItemPrice(item.itemPrice);
					actionDTO.setPitPrice(item.pitPrice);
					actionDTO.setQty(item.qty);
					actionDTO.setPitStatus(item.pitStatus);

					actionDTO.setPitItemOrder(item.pitItemOrder);

					auctionRoom.put(conut, new AuctionBroadcastService(actionDTO, messagingTemplate));

					conut++;
					return actionDTO;
				}).map(ActionDTOMappper.class::cast).toList();

		try {
			String mp = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listMap);
			System.out.println(mp);
		} catch (Exception e) {
		}

		messagingTemplate.convertAndSend("/action/buyItem", listMap);
	}
}