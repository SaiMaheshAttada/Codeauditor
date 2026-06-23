package com.codeauditor.ai.repository;

import com.codeauditor.ai.entity.AuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditHistoryRepository
        extends JpaRepository<AuditHistory, Long> {
}