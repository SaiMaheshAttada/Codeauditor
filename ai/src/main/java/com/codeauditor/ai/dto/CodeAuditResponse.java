package com.codeauditor.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeAuditResponse {
    private Long auditId;
    private String fileName;
    private String flaws;
    private String risks;
    private String recommendations;
    private List<String> correctedCode; // Change this to a List of Strings
}