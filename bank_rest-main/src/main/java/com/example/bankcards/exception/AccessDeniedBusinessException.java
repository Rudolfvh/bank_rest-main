package com.example.bankcards.exception;

public class AccessDeniedBusinessException extends RuntimeException {
    public AccessDeniedBusinessException() {
        super("Access denied");
    }
}