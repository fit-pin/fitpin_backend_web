package com.example.demo.repository;

import com.example.demo.entity.AuctionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Integer> {
        List<AuctionEntity> findByCompany(String company);
}
