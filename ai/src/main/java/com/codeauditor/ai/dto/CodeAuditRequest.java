package com.codeauditor.ai.dto;

public class CodeAuditRequest {

    private String fileName;

    private String code;


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }
}