package com.codeauditor.ai.controller;

import com.codeauditor.ai.dto.CodeAuditRequest;
import com.codeauditor.ai.dto.CodeAuditResponse;
import com.codeauditor.ai.entity.AuditHistory;
import com.codeauditor.ai.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    public ResponseEntity<CodeAuditResponse> auditCode(@RequestBody CodeAuditRequest request) {
        return ResponseEntity.ok(auditService.auditCode(request));
    }

    @GetMapping
    public ResponseEntity<List<AuditHistory>> getAllAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditHistory> getAuditById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAudit(@PathVariable Long id) {
        auditService.deleteAudit(id);
        return ResponseEntity.ok("Audit deleted successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Code Auditor Service Running");
    }

    @PostMapping(value = "/code", consumes = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<CodeAuditResponse> auditRawTextCode(@RequestBody String rawCode) {
        CodeAuditRequest request = new CodeAuditRequest();
        request.setFileName("Demoworkspace.java"); // Default placeholder for quick testing
        request.setCode(rawCode);

        return ResponseEntity.ok(auditService.auditCode(request));
    }
}