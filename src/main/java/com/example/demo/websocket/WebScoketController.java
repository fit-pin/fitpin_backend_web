package com.example.demo.websocket;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.websocket.ActionDTO.ItemList;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebScoketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final HashMap<Integer, List<ActionDTOMappper>> auctionRoom = new HashMap<>();

	private static int conut;

	// 클라이언트 메시지가 전송되는 곳
	// 이때 경로는 setApplicationDestinationPrefixes+설정값 즉 /recv/
	// Auction=경매 페이지
	@MessageMapping("/Auction/{auctionId}")
	private void AuctionConnect(@DestinationVariable int auctionId) throws Exception {
		log.info("경매 접속: " + auctionId);
		messagingTemplate.convertAndSend("/action/Auction/" + auctionId);
	}

	/**
	 * 고객이 의류를 구매한 경우 구독한 모든 클라이언트에게 전달
	 * 
	 * @param sendData 전송할 데이터
	 */
	public void sendBuyItem(ActionDTO sendData) {

		Stream<ItemList> stream = sendData.getItems().stream();

		List<ActionDTOMappper> listMap = stream.map((item) -> {
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

			return actionDTO;
		}).map(ActionDTOMappper.class::cast).toList();

		auctionRoom.put(conut, listMap);

		try {
			String mp = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listMap);
			System.out.println(mp);
		} catch (Exception e) {
		}

		messagingTemplate.convertAndSend("/action/buyItem", listMap);
		conut++;
	}
}