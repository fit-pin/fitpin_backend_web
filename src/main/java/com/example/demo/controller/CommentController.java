package com.example.demo.controller;

import com.example.demo.entity.CommentEntity;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiry/{inquiryId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 추가 엔드포인트
    @PostMapping
    public ResponseEntity<CommentEntity> addComment(
            @PathVariable Long inquiryId,
            @RequestBody String content
    ) {
        CommentEntity comment = commentService.addComment(inquiryId, content);
        return ResponseEntity.ok(comment);
    }

    // 특정 문의에 대한 댓글 목록 조회 엔드포인트
    @GetMapping
    public ResponseEntity<List<CommentEntity>> getCommentsByInquiryId(@PathVariable Long inquiryId) {
        List<CommentEntity> comments = commentService.getCommentsByInquiryId(inquiryId);
        return ResponseEntity.ok(comments);
    }
}
