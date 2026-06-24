package com.codeauditor.ai.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
class ErrorDetails {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorDetails(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
