package com.example.demo.controller;

import com.example.demo.entity.InquiryEntity;
import com.example.demo.service.InquiryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/inquiry")
public class InquiryController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final InquiryService inquiryService; // 서비스 계층

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public String submitInquiry(
            @RequestParam("name") String name,
            @RequestParam("queryType") String queryType,
            @RequestParam("subject") String subject,
            @RequestParam("queryContent") String queryContent,
            @RequestParam("privacy") String privacy,
            @RequestParam("password") String password,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment) {

        String filePath = null;

        if (attachment != null && !attachment.isEmpty()) {
            try {
                filePath = saveFile(attachment); // 파일 저장
            } catch (IOException e) {
                return "파일 업로드 중 오류가 발생했습니다.";
            }
        }

        // 데이터베이스에 저장
        InquiryEntity inquiry = new InquiryEntity(name, queryType, subject, queryContent, filePath, privacy, password);
        inquiryService.saveInquiry(inquiry);

        return "문의가 성공적으로 등록되었습니다.";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + File.separator + fileName);
        Files.copy(file.getInputStream(), path);
        return path.toString(); // 파일 경로 반환
    }
}
