package com.example.demo.repository;

import com.example.demo.entity.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DealerRepository extends JpaRepository<Dealer,Long> {
    Optional<Dealer> findByEmail(String email);
}
