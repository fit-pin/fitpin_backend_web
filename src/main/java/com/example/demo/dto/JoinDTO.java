package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JoinDTO {

    @NotBlank(message = "업체명은 필수 입력사항 입니다.")
    private String company;

    @NotBlank(message = "아이디는 필수 입력사항 입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String password;

    @NotBlank(message = "비밀번호는 필수 입력사항 입니다.")
    private String zipcode;

    @NotBlank(message = "주소입력은 필수 입력사항 입니다.")
    private String address1;

    @NotBlank(message = "주소입력은 필수 입력사항 입니다.")
    private String address2;

    @NotBlank(message = "전화번호는 필수 입력사항 입니다.")
    private String phone;

}
