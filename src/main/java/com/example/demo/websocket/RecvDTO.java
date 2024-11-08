package com.example.demo.websocket;

import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * 클라이언트가 서버에 전송하는 JSON
 */
@Getter
public class RecvDTO {
    private String token;
}

/**
 * 클라이언트가 서버에 전송하는 JSON
 */
@Getter
@Setter
class RecvPrice {
    private String token;
    private String company;
    private int price;

    private LocalDateTime time;

    @Nullable
    // lastPrice 저장용
    private String itemName;
}
