package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuctionEntity {
    @Id
    private int auctionId;

    private String company;

    private String auctionDetail;
}
