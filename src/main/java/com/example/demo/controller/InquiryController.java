package com.example.demo.controller;

import com.example.demo.entity.InquiryEntity;
import com.example.demo.service.InquiryService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inquiry")
@CrossOrigin(origins = "http://localhost:3000")
public class InquiryController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final InquiryService inquiryService; // 서비스 계층

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    // 비밀번호 검증 기능
    @PostMapping("/{id}/checkpwd")
    public ResponseEntity<String> checkPassword(
            @PathVariable("id") Long id,
            @RequestParam("password") String password
    ) {
        try {
            InquiryEntity inquiry = inquiryService.getInquiryById(id); // 특정 문의 조회

            // 비밀번호 검증
            if (inquiry.getPassword().equals(password)) {
                return ResponseEntity.ok("비밀번호가 맞습니다."); // 비밀번호가 맞으면 성공 메시지 반환
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비밀번호가 맞지 않습니다."); // 비밀번호가 틀리면 오류 메시지 반환
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("비밀번호 검증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 문의등록
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

    // 문의 목록 조회 (페이징 포함)
    @GetMapping
    public PagedModel<EntityModel<InquiryEntity>> getInquiries(
            @RequestParam(defaultValue = "0") int page,  // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "5") int size   // 한 페이지에 표시할 목록 수
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InquiryEntity> inquiriesPage = inquiryService.getInquiriesWithPagination(pageable);

        // 각 문의에 링크 추가
        List<EntityModel<InquiryEntity>> inquiryModels = inquiriesPage.getContent().stream()
                .map(inquiry -> EntityModel.of(inquiry,
                        linkTo(methodOn(InquiryController.class).getInquiryById(inquiry.getId())).withSelfRel()))
                .collect(Collectors.toList());

        // PagedModel로 변환
        return PagedModel.of(inquiryModels, new PagedModel.PageMetadata(size, page, inquiriesPage.getTotalElements()));
    }

    // 특정 문의 조회
    @GetMapping("/{id}")
    public EntityModel<InquiryEntity> getInquiryById(@PathVariable("id") Long id) {
        InquiryEntity inquiry = inquiryService.getInquiryById(id);

        // 개별 문의에 링크 추가
        return EntityModel.of(inquiry,
                linkTo(methodOn(InquiryController.class).getInquiryById(id)).withSelfRel(),
                linkTo(methodOn(InquiryController.class).getInquiries(0, 5)).withRel("all-inquiries"));
    }

    // 문의 수정 (비밀번호 검증 없이)
    @PutMapping("/{id}")
    public ResponseEntity<String> updateInquiry(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("queryType") String queryType,
            @RequestParam("subject") String subject,
            @RequestParam("queryContent") String queryContent,
            @RequestParam("privacy") String privacy,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment) {

        try {
            InquiryEntity inquiry = inquiryService.getInquiryById(id); // 특정 문의 조회

            String filePath = inquiry.getAttachmentPath(); // 기존 파일 경로

            // 첨부파일이 있으면 새로 저장하고 기존 파일은 삭제
            if (attachment != null && !attachment.isEmpty()) {
                if (filePath != null) {
                    deleteFile(filePath); // 기존 파일 삭제
                }
                filePath = saveFile(attachment); // 새 파일 저장
            }

            // 엔티티 업데이트
            inquiry.setName(name);
            inquiry.setQueryType(queryType);
            inquiry.setSubject(subject);
            inquiry.setQueryContent(queryContent);
            inquiry.setPrivacy(privacy);
            inquiry.setAttachmentPath(filePath);

            inquiryService.saveInquiry(inquiry); // 업데이트된 문의 저장

            return ResponseEntity.ok("문의가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 삭제 기능
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInquiry(
            @PathVariable("id") Long id
    ) {
        try {
            InquiryEntity inquiry = inquiryService.getInquiryById(id); // 특정 문의 조회

            if (inquiry.getAttachmentPath() != null) {
                deleteFile(inquiry.getAttachmentPath()); // 파일 삭제
            }
            inquiryService.deleteInquiry(id); // 삭제
            return ResponseEntity.ok("문의가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + File.separator + fileName);
        Files.copy(file.getInputStream(), path);
        return path.toString(); // 파일 경로 반환
    }

    private void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path); // 파일 삭제
    }

}
