package com.codeauditor.ai.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_history")
@Data
public class AuditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fileName;


    @Lob
    private String sourceCode;


    @Lob
    private String auditResult;


    private LocalDateTime createdAt;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getSourceCode() {
        return sourceCode;
    }


    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }


    public String getAuditResult() {
        return auditResult;
    }


    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}