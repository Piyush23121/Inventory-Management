package com.example.demo.repository;

import com.example.demo.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionLog t")
    BigDecimal sumTotalSales();

}
