package com.codeauditor.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAuditResult {
    private Object flaws;
    private Object risks;
    private Object recommendations;
    private List<String> correctedCode;
}