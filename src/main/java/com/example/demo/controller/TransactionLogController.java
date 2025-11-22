package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.entity.TransactionLog;
import com.example.demo.repository.TransactionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = " http://localhost:5173/")
public class TransactionLogController {

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @PreAuthorize(("hasAuthority('ADMIN')"))
    @GetMapping("transactions")
    public ResponseEntity<BaseResponseDTO<List<TransactionLog>>> getAllLogs(){
        List<TransactionLog> logs= transactionLogRepository.findAll();
        return ResponseEntity.ok(new BaseResponseDTO<>("Success", "Transaction logs fetch successfully" , logs));
    }
}
