package com.example.demo.websocket;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.demo.controller.AppNotificationController;

public class AuctionBroadcastService {
    private static final int AUCTION_TIME = 30;

    private ActionDTOMappper auctionData;
    private SimpMessagingTemplate messagingTemplate;
    private String sendUrl;

    private boolean activete = false;

    private RecvPrice lastPrice;

    public AuctionBroadcastService(ActionDTOMappper auctionData, SimpMessagingTemplate messagingTemplate) {
        this.auctionData = auctionData;
        this.messagingTemplate = messagingTemplate;
        this.sendUrl = "/action/Auction/" + auctionData.getAuctionId();
        this.lastPrice = new RecvPrice();
        lastPrice.setPrice(auctionData.getPitPrice());
    }

    public int getAuctionId() {
        return auctionData.getAuctionId();
    }

    public boolean isActivete() {
        return this.activete;
    }

    public void sendAuctionData(String token) {
        messagingTemplate.convertAndSend(sendUrl + "/roomData/" + token, auctionData);
    }

    public void sendPrice(RecvPrice body) {
        // 제시된 수선가격보다 높게 호가를 제시하지 못하도록
        if (lastPrice.getPrice() > body.getPrice()) {
            body.setTime(LocalDateTime.now());
            body.setItemName(auctionData.getItemName());
            messagingTemplate.convertAndSend(sendUrl + "/price", body);
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
                    messagingTemplate.convertAndSend(sendUrl + "/time", time.getActiontime());
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
            }
            appNotificationController.sendNotification(lastPrice, auctionData.getUserEmail());
            listener.recvAuctionEnd(auctionData.getAuctionId());
        }).start();
    }

}
