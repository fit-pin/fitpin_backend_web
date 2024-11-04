package com.example.demo.websocket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 서버가 클라이언트에게 전송하는 JSON
 */
@AllArgsConstructor
@Getter
public class ActionDTO {
    private String userEmail;
    private String userName;
    private String userAddr;
    private String userNumber;
    private String itemTotal;
    private String userAddrDetail;
    private List<ItemList> items;

    class ItemList {
        int itemKey;
        String itemName;
        String itemSize;
        int itemPrice;
        int qty;
        boolean pitStatus;
    }
}
