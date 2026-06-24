package com.codeauditor.ai.dto;

import lombok.Data;

@Data
public class CodeAuditRequest {
    private String fileName;
    private String code;
}