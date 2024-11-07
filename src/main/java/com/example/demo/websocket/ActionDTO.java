package com.example.demo.websocket;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.example.demo.websocket.ActionDTO.ItemList.PitItemOrder;

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
    private String userAddrDetail;
    private String userNumber;
    private int itemTotal;
    @NonNull
    private List<ItemList> items;

    @Getter
    @Setter
    public static class ItemList {
        int itemKey;
        String itemName;
        String itemSize;
        int itemPrice;
        int pitPrice;
        int qty;
        boolean pitStatus;
        @Nullable
        PitItemOrder pitItemOrder;

        @Getter
        @Setter
        public static class PitItemOrder {
            String itemType;
            String itemSize;
            float itemHeight;
            float itemShoulder;
            float itemChest;
            float itemSleeve;
            float frontrise;
            float itemWaists;
            float itemThighs;
            float itemHemWidth;
            float itemhipWidth;
        }
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
    private int pitPrice;
    private int itemPrice;
    private int qty;
    private boolean pitStatus;

    @Nullable
    PitItemOrder pitItemOrder;
}
