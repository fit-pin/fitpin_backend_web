package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

//DB에서 가져온 데이터를 저장할 객체
@Entity
@Setter
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    //userKey
    private int id;
    
    //업체명
    private String company;
    
    //아이디
    @Column(unique = true, nullable = false)
    private String username;

    //비밀번호
    private String password;

    //우편주소
    private String zipcode;

    //업체주소-1
    private String address1;

    //업체주소-2
    private String address2;

    //전화번호
    private String phone;

    //권한
    private String role;

    //가입날짜
    private LocalDate joinDate;

}
