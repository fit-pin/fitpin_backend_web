package com.example.demo.repository;

import com.example.demo.entity.InquiryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    @Query("SELECT i FROM InquiryEntity i ORDER BY i.id DESC ")
    Page<InquiryEntity> findAll(Pageable pageable);
}

