package com.example.demo.websocket;

import java.time.LocalDateTime;

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
class RecvPrice {
    private String token;
    private String company;
    private int price;

    @Setter
    private LocalDateTime  time; 
}
