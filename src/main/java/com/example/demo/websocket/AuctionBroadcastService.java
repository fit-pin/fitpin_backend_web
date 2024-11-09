package com.example.demo.websocket;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.demo.controller.AppNotificationController;

enum State {
    // 처음 생성시 (기본값)
    AUCTION_CREATE,
    // 경매 끝날시
    AUCTION_END,
    // 경매가 종료된 방에 들어오거나, 유효하지 않는경우
    AUCTION_UNDEFINDE,
    // 같은 업체로 들어온 경우
    AUCTION_DUPLICATE
}

public class AuctionBroadcastService {
    private static final int AUCTION_TIME = 30;

    private AuctionData auctionData;
    private SimpMessagingTemplate messagingTemplate;
    private String sendUrl = SendURL.SendRoomData;

    private boolean activete = false;

    private RecvPrice lastPrice;

    private HashMap<String, String> userList = new HashMap<>();

    public AuctionBroadcastService(AuctionData auctionData, SimpMessagingTemplate messagingTemplate) {
        this.auctionData = auctionData;
        this.messagingTemplate = messagingTemplate;
        this.sendUrl+= auctionData.getActionData().getAuctionId();
        this.lastPrice = new RecvPrice();
        lastPrice.setPrice(auctionData.getActionData().getPitPrice());
        lastPrice.setItemName(auctionData.getActionData().getItemName());
    }

    public int getAuctionId() {
        return auctionData.getActionData().getAuctionId();
    }

    public Repairlist getRepair() {
        return new Repairlist(auctionData, userList);
    }

    public boolean isActivete() {
        return this.activete;
    }

    /** 유저 접속 시 */
    public void sendAuctionData(AuctionConnectBody body) {
        // 최초 접속자이고 경매 호가가 비어있는 경우 최초 접속자가 수선 낙찰
        if (userList.isEmpty()) {
            lastPrice.setCompany(body.getCompany());
            lastPrice.setToken(body.getToken());
        }

        String user = userList.get(body.getToken());
        if (user == null) {
            messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.RoomData + body.getToken(), auctionData);
        } else {
            // 이미 존재하는 유저면 AUCTION_DUPLICATE 만 반환

            /*
             * TODO: 원래라면은 토큰 뿐 아니라 업체명도 검증하야 하는데
             * 혹시 몰라서 그냥 토큰으로만 검증함, 따라서 로그인만 따로 해주면 같은 계정이라도 정상작동함
             */
            AuctionData duplicate = new AuctionData();
            duplicate.setState(State.AUCTION_DUPLICATE.name());
            messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.RoomData + body.getToken(), duplicate);
        }
    }

    public void sendPrice(RecvPrice body) {
        // 제시된 수선가격보다 높게 호가를 제시하지 못하도록
        if (lastPrice.getPrice() > body.getPrice()) {
            body.setTime(LocalDateTime.now());
            messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.Price, body);
            lastPrice = body;
        }
    }

    public void createAuction(AuctiontEndListener listener, AppNotificationController appNotificationController) {
        this.activete = true;
        new Thread(() -> {
            try {
                ActionTimeDTO time = new ActionTimeDTO(AUCTION_TIME);
                for (int i = time.getActiontime(); i >= 0; i--) {
                    time.setActiontime(i);
                    messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.Price, time.getActiontime());
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
            }

            // * 로 모든 클라이언트에게 전송
            AuctionData endAuctionData = new AuctionData();
            endAuctionData.setRecvPrice(lastPrice);
            messagingTemplate.convertAndSend(sendUrl + "/roomData/*", lastPrice);
            appNotificationController.sendNotification(lastPrice, auctionData.getActionData().getUserEmail());
            listener.recvAuctionEnd(auctionData.getActionData().getAuctionId());
        }).start();
    }

}
