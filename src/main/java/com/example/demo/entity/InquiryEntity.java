package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class InquiryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String queryType;
    private String subject;
    private String queryContent;
    private String attachmentPath;
    private String privacy;
    private String password;

    public InquiryEntity() {}

    public InquiryEntity(String name, String queryType, String subject, String queryContent, String attachmentPath,  String privacy, String password) {
        this.name = name;
        this.queryType = queryType;
        this.subject = subject;
        this.queryContent = queryContent;
        this.attachmentPath = attachmentPath;
        this.privacy = privacy;
        this.password = password;
    }

}
