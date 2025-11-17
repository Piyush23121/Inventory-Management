package com.example.demo.exception;

import java.net.BindException;


public class MethodArgumentNotValidException extends BindException {
    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}
