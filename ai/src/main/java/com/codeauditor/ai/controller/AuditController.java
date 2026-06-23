package com.codeauditor.ai.controller;

import com.codeauditor.ai.dto.CodeAuditRequest;
import com.codeauditor.ai.dto.CodeAuditResponse;
import com.codeauditor.ai.entity.AuditHistory;
import com.codeauditor.ai.service.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audits")
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Analyze code using Ollama and save audit history
     */
    @PostMapping
    public ResponseEntity<CodeAuditResponse> auditCode(
            @RequestBody CodeAuditRequest request) throws JsonProcessingException {

        CodeAuditResponse result = auditService.auditCode(request);

        return ResponseEntity.ok(result);
    }

    /**
     * Get all audit records
     */
    @GetMapping
    public ResponseEntity<List<AuditHistory>> getAllAudits() {

        return ResponseEntity.ok(
                auditService.getAllAudits()
        );
    }

    /**
     * Get audit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditHistory> getAuditById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auditService.getAuditById(id)
        );
    }

    /**
     * Delete audit by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAudit(
            @PathVariable Long id) {

        auditService.deleteAudit(id);

        return ResponseEntity.ok(
                "Audit deleted successfully"
        );
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {

        return ResponseEntity.ok(
                "Code Auditor Service Running"
        );
    }
}