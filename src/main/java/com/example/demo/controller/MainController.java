package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.websocket.ActionDTO;
import com.example.demo.websocket.WebScoketController;
import com.example.demo.websocket.ActionDTO.ItemList;

@RestController
@Controller
@ResponseBody
public class MainController {

    @Autowired
    public WebScoketController webScoketController;

    @GetMapping("/")
    public String mainP() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // 세션 정보확인(사용자 정보 확인)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        List<ItemList> list = new ArrayList<>();
        ItemList itemList = new ItemList();
        itemList.setItemKey(6);
        itemList.setItemName("피그먼트 롱슬리브");
        itemList.setItemSize("M");
        itemList.setItemPrice(2000);
        itemList.setQty(1);
        itemList.setPitStatus(false);
        list.add(itemList);

        webScoketController.sendBuyItem(
                new ActionDTO("da864268@naver.com", "석정현", "서울특별시 구로구 경인로 445", "010-1234-1234", 1, "3호관", list));

        return "main Controller : " + name + " / " + role;

    }
}
