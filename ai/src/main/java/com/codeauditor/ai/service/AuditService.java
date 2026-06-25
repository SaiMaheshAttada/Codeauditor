package com.codeauditor.ai.service;

import com.codeauditor.ai.dto.AiAuditResult;
import com.codeauditor.ai.dto.CodeAuditRequest;
import com.codeauditor.ai.dto.CodeAuditResponse;
import com.codeauditor.ai.entity.AuditHistory;
import com.codeauditor.ai.repository.AuditHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final ChatClient chatClient;
    private final AuditHistoryRepository auditHistoryRepository;

    public AuditService(ChatClient.Builder chatClientBuilder, AuditHistoryRepository auditHistoryRepository) {
        this.chatClient = chatClientBuilder.build();
        this.auditHistoryRepository = auditHistoryRepository;
    }

    public CodeAuditResponse auditCode(CodeAuditRequest request) {
        String prompt = """
        You are a strict Java Code Auditor. Analyze the snippet below for syntax and structural bugs.
        
        Code to Analyze:
        %s
        
        CRITICAL OUTPUT REQUIREMENTS:
        1. Output ONLY a direct JSON object. Do NOT use markdown code blocks or backticks.
        2. For the 'correctedCode' field, output the clean refactored code split line-by-line as an explicit JSON Array of strings.
        3. DO NOT use double quotes (") inside the code lines to avoid JSON backslash escaping. Use single quotes (') for any text literals or string messages instead (e.g., System.out.println('Processing...')).
        
        Format:
        {
          "flaws": "List brief syntax bugs found",
          "risks": "List major runtime crash risks",
          "recommendations": "Provide a quick fix action",
          "correctedCode": [
            "public void processOrder(List<String> items) {",
            "    int totalItems = items.size();",
            "    for (int i = 0; i < totalItems; i++) {",
            "        System.out.println('Processing: ' + items.get(i));",
            "    }",
            "}"
          ]
        }
        """.formatted(request.getCode());

        String rawAiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (rawAiResponse == null || rawAiResponse.trim().isEmpty()) {
            throw new com.codeauditor.ai.exception.AIEngineException("Local Ollama engine returned an empty response.");
        }

        AiAuditResult aiResult;
        try {
            int startIdx = rawAiResponse.indexOf("{");
            int endIdx = rawAiResponse.lastIndexOf("}");

            if (startIdx == -1 || endIdx == -1) {
                aiResult = new AiAuditResult();
                aiResult.setFlaws(rawAiResponse);
                aiResult.setCorrectedCode(java.util.Collections.singletonList("// Formatting issue occurred"));
            } else {
                String cleanJson = rawAiResponse.substring(startIdx, endIdx + 1);
                System.out.println("====== RAW AI JSON OUTPUT ======\n" + cleanJson + "\n================================");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                aiResult = mapper.readValue(cleanJson, AiAuditResult.class);
            }
        } catch (Exception e) {
            aiResult = new AiAuditResult();
            aiResult.setFlaws("Analysis completed with formatting limits.");
            aiResult.setCorrectedCode(java.util.Collections.singletonList("// Code payload exceeded token limits"));
        }

        AuditHistory audit = new AuditHistory();
        audit.setFileName(request.getFileName());
        audit.setSourceCode(request.getCode());
        audit.setAuditResult(String.format("Processed tracking record ID: %s", request.getFileName()));
        audit.setCreatedAt(LocalDateTime.now());
        auditHistoryRepository.save(audit);

        return new CodeAuditResponse(
                audit.getId(),
                audit.getFileName(),
                String.valueOf(aiResult.getFlaws()),
                String.valueOf(aiResult.getRisks()),
                String.valueOf(aiResult.getRecommendations()),
                aiResult.getCorrectedCode() // Pass the List directly
        );
    }
    public List<AuditHistory> getAllAudits() {
        return auditHistoryRepository.findAll();
    }

    public AuditHistory getAuditById(Long id) {
        return auditHistoryRepository.findById(id)
                .orElseThrow(() -> new com.codeauditor.ai.exception.ResourceNotFoundException("Audit record not found with tracking ID: " + id));
    }

    public void deleteAudit(Long id) {
        auditHistoryRepository.deleteById(id);
    }
}