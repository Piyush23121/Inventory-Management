// src/main/java/com/example/demo/serviceImpl/AdminServiceImpl.java
package com.example.demo.serviceImpl;

import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.entity.RoleType;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final TransactionLogRepository txRepo;
    private final ProductService productService;

    public AdminServiceImpl(UserRepository userRepo,
                            ProductRepository productRepo,
                            TransactionLogRepository txRepo,
                            ProductService productService) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.txRepo = txRepo;
        this.productService = productService;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepo.count();
        long totalDealers = userRepo.countByRole(RoleType.DEALER);
        long totalCustomers = userRepo.countByRole(RoleType.CUSTOMER);
        long totalProducts = productRepo.count();

        BigDecimal totalSales = txRepo.sumTotalSales(); // never null due to COALESCE
        int lowStockCount = productService.getLowStockProducts().size();

        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setTotalUsers(totalUsers);
        dto.setTotalDealers(totalDealers);
        dto.setTotalCustomers(totalCustomers);
        dto.setTotalProducts(totalProducts);
        dto.setTotalSales(totalSales);
        dto.setLowStockCount(lowStockCount);
        return dto;
    }
}
