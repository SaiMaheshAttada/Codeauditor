package com.codeauditor.ai.dto;

import lombok.Data;

@Data
public class CodeAuditResponse {

    private Long auditId;

    private String fileName;

    private String flaws;

    private String risks;

    private String recommendations;

    public CodeAuditResponse() {
    }

    public CodeAuditResponse(
            Long auditId,
            String fileName,
            String flaws,
            String risks,
            String recommendations) {

        this.auditId = auditId;
        this.fileName = fileName;
        this.flaws = flaws;
        this.risks = risks;
        this.recommendations = recommendations;
    }

    // getters setters
}