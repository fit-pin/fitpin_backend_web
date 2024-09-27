package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    //userKey
    private Long id;

    //아이디
    private String username;

    //로그인시 토큰발급
    private String refresh;
    
    //로그인시간
    private String expiration;
}
