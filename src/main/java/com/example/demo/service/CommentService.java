package com.example.demo.service;

import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.InquiryEntity;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    // 댓글 추가 메서드
    public CommentEntity addComment(Long inquiryId, String content) {
        // 해당 문의 조회
        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("해당 문의를 찾을 수 없습니다."));

        // 새로운 댓글 생성
        CommentEntity comment = new CommentEntity(inquiry, content);
        return commentRepository.save(comment); // 댓글 저장
    }

    // 특정 문의의 댓글 목록 조회 메서드
    public List<CommentEntity> getCommentsByInquiryId(Long inquiryId) {
        // 해당 문의 조회
        InquiryEntity inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("해당 문의를 찾을 수 없습니다."));

        // 해당 문의에 속한 댓글 리스트 조회
        return commentRepository.findByInquiry(inquiry);
    }
}
