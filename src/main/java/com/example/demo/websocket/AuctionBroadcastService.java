package com.example.demo.websocket;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.demo.controller.AppNotificationController;

enum State {
    // 처음 생성시 (기본값)
    AUCTION_CREATE,
    // 경매가 진행중인 경우
    AUCTION_PROGRESS,
    // 경매 끝날시
    AUCTION_END,
    // 경매가 종료된 방에 들어오거나, 유효하지 않는경우
    AUCTION_UNDEFINDE,
}

public class AuctionBroadcastService {
    private static final int AUCTION_TIME = 30;

    private AuctionData auctionData;
    private SimpMessagingTemplate messagingTemplate;
    private String sendUrl = SendURL.SendRoomData;

    private boolean activete = false;

    private RecvPrice lastPrice;

    private ArrayList<String> userList = new ArrayList<>();

    public AuctionBroadcastService(AuctionData auctionData, SimpMessagingTemplate messagingTemplate) {
        this.auctionData = auctionData;
        this.messagingTemplate = messagingTemplate;
        this.sendUrl += auctionData.getActionData().getAuctionId();
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
        messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.RoomData + body.getToken(), auctionData);
        if (userList.isEmpty()) {
            lastPrice.setCompany(body.getCompany());
            lastPrice.setToken(body.getToken());
            auctionData.setState(State.AUCTION_PROGRESS.name());
        }
        userList.add(body.getCompany());
    }

    public void sendPrice(RecvPrice body) {
        // 제시된 수선가격보다 높게 호가를 제시하지 못하도록
        if (lastPrice.getPrice() > body.getPrice()) {
            body.setTime(LocalDateTime.now());
            messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.Price, body);

            // 수선 페이지 에서도 이걸 갱신 받을 수 있게 설정
            auctionData.getActionData().setPitPrice(body.getPrice());

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
                    messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.Time, time.getActiontime());
                    Thread.sleep(1000);
                }
                AuctionData endPrice = new AuctionData();
                endPrice.setRecvPrice(lastPrice);
                endPrice.setState(State.AUCTION_END.name());

                // /all 경로로 전송 (* 같은 와일드 카드 허용 안함)
                messagingTemplate.convertAndSend(sendUrl + SendURL.SendRoom.RoomData + "all", endPrice);
                appNotificationController.sendNotification(lastPrice, auctionData.getActionData().getUserEmail());
            } catch (Exception e) {
            }

            listener.recvAuctionEnd(auctionData.getActionData().getAuctionId());
        }).start();
    }

}
