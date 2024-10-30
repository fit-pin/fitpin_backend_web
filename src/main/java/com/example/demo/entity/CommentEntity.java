package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private InquiryEntity inquiry; // InquiryEntity와 다대일 관계 설정

    private String content; // 댓글 내용
    private LocalDate createdAt; // 댓글 작성 시간

    public CommentEntity() {}

    public CommentEntity(InquiryEntity inquiry, String content) {
        this.inquiry = inquiry;
        this.content = content;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDate.now(); // 댓글 작성 시간 설정
    }
}
