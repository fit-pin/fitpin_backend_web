package com.example.demo.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.controller.AppNotificationController;
import com.example.demo.repository.AuctionRepository;
import com.example.demo.websocket.ActionDTO.ItemList;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 서버가 보내는 메시지 */
class SendURL {
	/**
	 * 수선 페이지를 접속한경우 기존에 하고있던 경매를 보냄
	 * 
	 * @implNote 원본주소: /action/repair/connect/{token}
	 */
	public static final String SendRepairList = "/action/repair/connect/";
	/** 고객이 수선을 요청한경우 보내는 메시지 */
	public static final String SendBuyItem = "/action/repair/buyItem";
	/** 경매가 끝난경우 수선페이지에 보내는 메시지 */
	public static final String SendEndItem = "/action/repair/endItem";

	/**
	 * 경매에 진행에 필요한 정보 요청 URL
	 * 
	 * @implNote 원본주소: /action/auction/{auctionId}
	 */
	public static final String SendRoomData = "/action/auction/";

	/**관리자 경매 종료 요청 완료 알림 */
	public static final String SendForceEnd = "/action/admin/end";

	/** SendRoomData 하위 URL들 */
	static class SendRoom {
		/**
		 * 경매 진행상황 전송
		 * 
		 * @implNote 원본주소: /roomData/{token}
		 */
		public static final String RoomData = "/roomdata/";
		/** 경매 호가 전송 */
		public static final String Price = "/price";
		/** 경매 남은시간 정송 */
		public static final String Time = "/time";
	}
}

/** 클라이언트가 보내는 메시지 */
class RecvURL {
	/** 수선 페이지 접속 */
	public static final String RepairConnect = "/repair/connect";

	/** 경매 접속 */
	public static final String AuctionConnect = "/auction/{auctionId}/connect";

	/** 경매 호가 제시 */
	public static final String AuctionPrice = "/auction/{auctionId}/price";

	/**  관리자 경매 종료 요청 */
	public static final String ForceEnd = "/admin/end/{auctionId}";
}

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebScoketController {
	private final SimpMessagingTemplate messagingTemplate;
	private final HashMap<Integer, AuctionBroadcastService> auctionRoom = new HashMap<>();

	private static int conut;

	@Autowired
	AppNotificationController appNotificationController;

	@Autowired
	AuctionRepository auctionRepository;

	// 클라이언트 메시지가 전송되는 곳
	// 이때 경로는 setApplicationDestinationPrefixes+설정값 즉 /recv/
	@MessageMapping(RecvURL.AuctionConnect)
	private void AuctionConnect(@DestinationVariable int auctionId, AuctionConnectBody body) {
		log.info("경매 접속: " + auctionId);

		AuctionBroadcastService room = auctionRoom.get(auctionId);
		if (room != null) {
			if (!room.isActivete()) {
				room.createAuction(id -> {
					log.info(id + ":  경매 종료");
					// 경매 종료 시 Map 에서 지우서 GC가 메모리 할당을 해제 하도록 함
					auctionRoom.remove(id);
					messagingTemplate.convertAndSend(SendURL.SendEndItem, id);
				}, appNotificationController);
			}
			room.sendAuctionData(body);
		} else {
			log.info(auctionId + ": 방이 존재하지 않음");
			AuctionData undefinedData = new AuctionData();
			undefinedData.setState(State.AUCTION_UNDEFINDE.name());
			messagingTemplate.convertAndSend(
					SendURL.SendRoomData + auctionId + SendURL.SendRoom.RoomData + body.getToken(), undefinedData);
		}
	}

	@MessageMapping(RecvURL.RepairConnect)
	private void repairConnect(AuctionConnectBody body) {
		log.info(body.getCompany() + ": 수선 페이지 접속");
		ArrayList<Repairlist> list = new ArrayList<>();
		auctionRoom.forEach((k, v) -> {
			list.add(v.getRepair());
		});

		messagingTemplate.convertAndSend(SendURL.SendRepairList + body.getToken(), list);
	}

	@MessageMapping(RecvURL.AuctionPrice)
	private void AuctionPrice(@DestinationVariable int auctionId, RecvPrice body) {
		AuctionBroadcastService room = auctionRoom.get(auctionId);
		if (room != null) {
			log.info(auctionId + " 에서 제시된 호가:" + body.getPrice());
			room.sendPrice(body);
		}
	}

	@MessageMapping(RecvURL.ForceEnd)
	private void forceEndReqests(@DestinationVariable int auctionId) {
		log.info("들어옴");
		AuctionBroadcastService room = auctionRoom.get(auctionId);
		HashMap<String, Object> response = new HashMap<>();
		if (room != null) {
			room.forceReqestEnd();
			response.put("stats", true);
			response.put("auctionId", auctionId);
		} else {
			response.put("stats", false);
		}
		messagingTemplate.convertAndSend(SendURL.SendForceEnd, response);
	}

	// 고객이 의류를 구매한 경우 구독한 모든 클라이언트에게 전달
	public void sendBuyItem(ActionDTO sendData) {

		Stream<ItemList> stream = sendData.getItems().stream();

		List<ActionDTOMappper> listMap = stream.filter((item) -> item.pitStatus)
				.map((item) -> {
					ActionDTOMappper actionDTO = new ActionDTOMappper();
					AuctionData auctionData = new AuctionData();
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

					auctionData.setActionData(actionDTO);

					auctionRoom.put(conut,
							new AuctionBroadcastService(auctionData, messagingTemplate, auctionRepository));

					conut++;
					return actionDTO;
				}).map(ActionDTOMappper.class::cast).toList();

		try {
			String mp = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listMap);
			System.out.println(mp);
		} catch (Exception e) {
		}

		if (!listMap.isEmpty()) {
			messagingTemplate.convertAndSend(SendURL.SendBuyItem, listMap);
		}

	}
}