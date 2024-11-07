package com.example.demo.websocket;

import java.time.LocalDateTime;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class AuctionBroadcastService {
    private static final int AUCTION_TIME = 60;

    private ActionDTOMappper auctionData;
    private SimpMessagingTemplate messagingTemplate;
    private String sendUrl;

    private boolean activete = false;

    public AuctionBroadcastService(ActionDTOMappper auctionData, SimpMessagingTemplate messagingTemplate) {
        this.auctionData = auctionData;
        this.messagingTemplate = messagingTemplate;
        this.sendUrl = "/action/Auction/" + auctionData.getAuctionId();
    }

    public int getAuctionId() {
        return auctionData.getAuctionId();
    }

    public boolean isActivete() {
        return this.activete;
    }

    public void sendAuctionData(String token) {
        messagingTemplate.convertAndSend(sendUrl+"/roomData/"+token, auctionData);
    }

    public void sendPrice(RecvPrice body) {
        body.setTime(LocalDateTime.now());
        messagingTemplate.convertAndSend(sendUrl+"/price", body);
    }

    public void createAuction() {
        this.activete = true;
        new Thread(() -> {
            try {
                ActionTimeDTO time = new ActionTimeDTO(AUCTION_TIME);
                while (true) {
                    if (time.getActiontime() < 0) {
                        break;
                    }
                    messagingTemplate.convertAndSend(sendUrl+"/time", time.getActiontime());
                    time.setActiontime(time.getActiontime() - 1);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

}
