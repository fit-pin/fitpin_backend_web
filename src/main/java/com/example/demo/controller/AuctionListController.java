package com.example.demo.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AuctionEntity;
import com.example.demo.repository.AuctionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    public List<ObjectNode> getAuctionData(@PathVariable String company) {

        List<AuctionEntity> res = auctionRepository.findByCompany(company);
        List<ObjectNode> result = new ArrayList<>();

        res.forEach((item) -> {
            try {
                ObjectNode node = (ObjectNode) new ObjectMapper().readTree(item.getAuctionDetail());
                if (node.isObject()) {
                    ObjectNode objectNode = (ObjectNode) node;
                    objectNode.put("repairId", item.getAuctionKey());
                }
                result.add(node);
            } catch (Exception e) {
            }
        });

        return result;
    }
}
