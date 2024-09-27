package com.example.demo.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @GetMapping("/token")
    public ResponseEntity<?> getRefreshToken(HttpServletRequest request) {
        // request에서 쿠키 배열을 가져옴
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 'refresh'라는 이름의 쿠키를 찾음
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    // 쿠키 값 반환
                    String refreshToken = cookie.getValue();
                    return ResponseEntity.ok().body("{\"refreshToken\": \"" + refreshToken + "\"}");
                }
            }
        }
        // 쿠키가 없을 경우
        return ResponseEntity.status(401).body("{\"message\": \"No refresh token found\"}");
    }
}
