package com.example.demo.websocket;

import lombok.Getter;


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
}
