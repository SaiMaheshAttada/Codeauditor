package com.codeauditor.ai.dto;

import lombok.Data;

@Data
public class AiAuditResult {
    private String flaws;
    private String risks;
    private String recommendations;
}