package com.example.demo.websocket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 서버가 클라이언트에게 전송하는 JSON
 */
@Getter
@AllArgsConstructor
public class ActionDTO {
    private String userEmail;
    private String userName;
    private String userAddr;
    private String userNumber;
    private int itemTotal;
    private String userAddrDetail;
    private List<ItemList> items;

    @Getter
    @Setter
    public static class ItemList {
        int itemKey;
        String itemName;
        String itemSize;
        int itemPrice;
        int qty;
        boolean pitStatus;
    }
}

@Getter
@Setter
/**
 * 제품 매핑용
 */
class ActionDTOMappper {
    private int auctionId;
    private String userEmail;
    private String userName;
    private String userAddr;
    private String userNumber;
    private int itemTotal;
    private String userAddrDetail;
    
    private int itemKey;
    private String itemName;
    private String itemSize;
    private int itemPrice;
    private int qty;
    private boolean pitStatus;
}
