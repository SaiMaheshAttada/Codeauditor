package com.codeauditor.ai.service;

import com.codeauditor.ai.dto.AiAuditResult;
import com.codeauditor.ai.dto.CodeAuditRequest;
import com.codeauditor.ai.dto.CodeAuditResponse;
import com.codeauditor.ai.entity.AuditHistory;
import com.codeauditor.ai.repository.AuditHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final ChatClient chatClient;
    private final AuditHistoryRepository auditHistoryRepository;


    public AuditService(
            ChatClient.Builder chatClientBuilder,
            AuditHistoryRepository auditHistoryRepository) {

        this.chatClient = chatClientBuilder.build();
        this.auditHistoryRepository = auditHistoryRepository;
    }


    // Analyze code using Ollama and save result
    public CodeAuditResponse auditCode(CodeAuditRequest request) throws JsonProcessingException {


        String prompt = """
STRICT INSTRUCTION:

Return ONLY VALID JSON.

Example:

{
  "flaws":"Optional.get() used",
  "risks":"NoSuchElementException",
  "recommendations":"Use orElseThrow()"
}

Do NOT write:
- Here is the analysis
- Markdown
- Explanations
- Code blocks

Analyze:

%s
""".formatted(request.getCode());


        String aiResponse = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
        ObjectMapper mapper = new ObjectMapper();

        AiAuditResult aiResult =
                mapper.readValue(
                        aiResponse,
                        AiAuditResult.class
                );


        AuditHistory audit = new AuditHistory();

        audit.setFileName(request.getFileName());

        audit.setSourceCode(
                request.getCode()
        );

        audit.setAuditResult(
                aiResponse
        );

        audit.setCreatedAt(
                LocalDateTime.now()
        );
        auditHistoryRepository.save(audit);
        CodeAuditResponse response =
                new CodeAuditResponse(
                        audit.getId(),
                        audit.getFileName(),
                        aiResult.getFlaws(),
                        aiResult.getRisks(),
                        aiResult.getRecommendations()
                );

        return response;


        
    }



    // Get all audit history
    public List<AuditHistory> getAllAudits(){

        return auditHistoryRepository.findAll();
    }



    // Get audit by ID
    public AuditHistory getAuditById(Long id){

        return auditHistoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Audit not found with id : " + id
                        )
                );
    }



    // Delete audit
    public void deleteAudit(Long id){

        auditHistoryRepository.deleteById(id);
    }

}