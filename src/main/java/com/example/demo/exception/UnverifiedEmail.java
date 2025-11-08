package com.example.demo.exception;

public class UnverifiedEmail extends RuntimeException {
    public UnverifiedEmail(String message) {
        super(message);
    }
}
