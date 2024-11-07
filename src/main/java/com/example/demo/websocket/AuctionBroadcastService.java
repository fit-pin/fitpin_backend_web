package com.example.demo.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class AuctionBroadcastService {
    private static final String AuctionSend = "/action/Auction/";

    private ActionDTOMappper service;
    private SimpMessagingTemplate messagingTemplate;

    public AuctionBroadcastService(ActionDTOMappper service, SimpMessagingTemplate messagingTemplate) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
    }

}
