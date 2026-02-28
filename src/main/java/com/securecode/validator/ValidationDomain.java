package com.securecode.validator;

public enum ValidationDomain {
    AI("ai.yaml"),
    AUTH("auth.yaml"),
    AUTHORIZATION("authorization.yaml"),
    CONFIG("config.yaml"),
    INPUT("input.yaml");

    private final String fileName;

    ValidationDomain(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
