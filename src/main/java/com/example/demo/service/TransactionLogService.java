package com.example.demo.service;

import com.example.demo.entity.TransactionLog;
import com.example.demo.repository.TransactionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionLogService {

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public  void saveLog(Long productId , Long userId, String changeType, int quantityChanged){
        TransactionLog log = new TransactionLog();
        log.setProductId(productId);
        log.setUserID(userId);
        log.setChangeType(changeType);
        log.setQuantityChanged(quantityChanged);

        transactionLogRepository.save(log);
    }
}
