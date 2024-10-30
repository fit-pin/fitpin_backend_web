package com.example.demo.repository;

import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository <CommentEntity, Long> {
    List<CommentEntity> findByInquiry(InquiryEntity inquiry);
}
