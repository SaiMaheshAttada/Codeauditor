package com.codeauditor.ai.service;

import com.codeauditor.ai.dto.AiAuditResult;
import com.codeauditor.ai.dto.CodeAuditRequest;
import com.codeauditor.ai.dto.CodeAuditResponse;
import com.codeauditor.ai.entity.AuditHistory;
import com.codeauditor.ai.exception.ResourceNotFoundException;
import com.codeauditor.ai.repository.AuditHistoryRepository;
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
        // Enforce rigid structure via Spring AI's BeanOutputConverter
        BeanOutputConverter<AiAuditResult> outputConverter = new BeanOutputConverter<>(AiAuditResult.class);

        String promptTemplate = """
                Analyze the following Java code snippet for architectural defects, security risks, or code smells.
                
                Code Snippet to Analyze:
                %s
                
                {format}
                """;

        String prompt = String.format(promptTemplate, request.getCode());

        // Execute inference and natively parse object map
        AiAuditResult aiResult = chatClient.prompt()
                .user(userSpec -> userSpec.text(prompt).param("format", outputConverter.getFormat()))
                .call()
                .entity(outputConverter);

        if (aiResult == null) {
            throw new com.codeauditor.ai.exception.AIEngineException("Local Ollama inference engine failed to compute a structured code analysis.");
        }

        // Persist history log record
        AuditHistory audit = new AuditHistory();
        audit.setFileName(request.getFileName());
        audit.setSourceCode(request.getCode());
        audit.setAuditResult(String.format("Flaws: %s | Risks: %s", aiResult.getFlaws(), aiResult.getRisks()));
        audit.setCreatedAt(LocalDateTime.now());
        auditHistoryRepository.save(audit);

        return new CodeAuditResponse(
                audit.getId(),
                audit.getFileName(),
                aiResult.getFlaws(),
                aiResult.getRisks(),
                aiResult.getRecommendations()
        );
    }

    public List<AuditHistory> getAllAudits() {
        return auditHistoryRepository.findAll();
    }

    public AuditHistory getAuditById(Long id) {
        return auditHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit record not found with tracking id : " + id));
    }

    public void deleteAudit(Long id) {
        auditHistoryRepository.deleteById(id);
    }
}