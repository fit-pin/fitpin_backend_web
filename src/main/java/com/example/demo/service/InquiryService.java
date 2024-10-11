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
}
