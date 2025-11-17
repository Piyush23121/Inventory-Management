package com.example.demo.exception;

import com.example.demo.dto.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleResourceNotFound(ResourceNotFoundException ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error",ex.getMessage(),null), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleInvalidInput(InvalidInputException ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error", ex.getMessage(), null),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationFailureException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleAuthenticationFailure(AuthenticationFailureException ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error",ex.getMessage(),null),HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleAccessDenied(AccessDeniedException ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error", ex.getMessage(), null),HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleGeneralException(Exception ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("Error, Something went wrong:", ex.getMessage(), null),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleImageUpload(ImageUploadException ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error , Failed to upload failed",ex.getMessage(),null),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnverifiedEmail.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleUnverifiedEmail(UnverifiedEmail ex){
        return new ResponseEntity<>(new BaseResponseDTO<>("error, Please Verify Your Email",ex.getMessage(),null),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleValidation(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return  new ResponseEntity<>(new BaseResponseDTO<>("error",message,null),HttpStatus.BAD_REQUEST);
    }

}
