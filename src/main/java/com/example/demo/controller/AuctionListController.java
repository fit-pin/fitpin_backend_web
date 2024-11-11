package com.example.demo.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AuctionEntity;
import com.example.demo.repository.AuctionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@ResponseBody
public class AuctionListController {

    @Autowired
    AuctionRepository auctionRepository;

    @GetMapping(path = "/getauction/{company}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JsonNode> getAuctionData(@PathVariable String company) {

        List<AuctionEntity> res = auctionRepository.findByCompany(company);
        List<JsonNode> result = new ArrayList<>();

        res.forEach((item) -> {
            try {
                result.add(new ObjectMapper().readTree(item.getAuctionDetail()));
            } catch (Exception e) {
            }
        });

        return result;
    }
}
