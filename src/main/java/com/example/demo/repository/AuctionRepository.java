package com.example.demo.repository;

import com.example.demo.entity.AuctionEntity;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Transactional

public interface AuctionRepository extends JpaRepository<AuctionEntity, Integer> {
        List<AuctionEntity> findByCompany(String company);

        @Modifying
        @Query(value = "insert into AuctionEntity (auctionId, company, auctionDetail) values (:auctionId, :company, :auctionDetail) on duplicate key update company = :company, auctionDetail = :auctionDetail", nativeQuery = true)
        void insertOrUpdate(@Param("auctionId") int auctionId, @Param("company") String company,
                        @Param("auctionDetail") String auctionDetail);
}
