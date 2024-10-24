package com.example.demo.controller;


import com.example.demo.entity.RefreshEntity;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
public class ReissueController {
    private final JWTUtil jwtUtil;

    private final RefreshRepository refreshRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {

        // 클라이언트로부터 요청 본문에서 refresh 토큰 가져오기
        String refresh = requestBody.get("refreshToken");

        // 쿠키 사용 X
//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null){
//            for (Cookie cookie : cookies) {
//
//                if (cookie.getName().equals("refresh")) {
//
//                    refresh = cookie.getValue();
//                }
//            }
//        }


        if (refresh == null || refresh.isEmpty()) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //Refresh token이 만료되었는지 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String userId = jwtUtil.getUsername(refresh);
        String userRole = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", userId, userRole, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userId, userRole, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(userId, newRefresh, 86400000L);

        //새로운 Access Token 응답에 포함
        response.setHeader("access", newAccess);

        //쿠키에 새로운 Refresh token 추가
        //쿠키 사용 X
//        response.addCookie(createCookie("refresh", newRefresh));

        // 응답 바디에 새 토큰 정보 전달
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", newAccess);
        tokens.put("refresh", newRefresh);

        return new ResponseEntity<>(tokens,HttpStatus.OK);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    // 쿠키사용 X
//    private Cookie createCookie(String key, String value) {
//
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(24*60*60);
//        cookie.setSecure(true);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//
//        return cookie;
//    }
}
