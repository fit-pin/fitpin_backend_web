package com.example.demo.service;

import com.example.demo.entity.InquiryEntity;
import com.example.demo.repository.InquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public void saveInquiry(InquiryEntity inquiryEntity) {
        inquiryRepository.save(inquiryEntity);
    }

    public Page<InquiryEntity> getInquiriesWithPagination(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }

    public InquiryEntity getInquiryById(Long id) {
        return inquiryRepository.findById(id).orElseThrow(() ->
                new RuntimeException("해당 ID의 문의가 존재하지 않습니다."));
    }

    // 해당 ID의 문의가 있는지 확인한 후 삭제
    public void deleteInquiry(Long id) {
        InquiryEntity inquiry = getInquiryById(id);  // 존재 여부 확인 (예외 처리 포함)
        inquiryRepository.deleteById(inquiry.getId());
    }
}
