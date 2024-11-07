package com.example.demo.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.websocket.ActionDTO;
import com.example.demo.websocket.WebScoketController;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@ResponseBody
public class AppOrderController {

    @Autowired
    public WebScoketController webScoketController;

    @PostMapping("/weborder")
    public HashMap<String, String> postMethodName(@RequestBody ActionDTO body) {
        webScoketController.sendBuyItem(body);
        HashMap<String, String> res = new HashMap<>();
        
        res.put("message", "성공적으로 전달완료");
        return res;
    }

    
}