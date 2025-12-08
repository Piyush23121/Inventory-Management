// src/main/java/com/example/demo/controller/AdminDashboardController.java
package com.example.demo.controller;

import com.example.demo.dto.BaseResponseDTO;
import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.dto.LowStockAdminDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Product;
import com.example.demo.service.AdminService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")// <- no /api prefix here
public class AdminDashboardController {

    private final AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;


    public AdminDashboardController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponseDTO<DashboardStatsDTO>> getStats() {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        BaseResponseDTO<DashboardStatsDTO> resp = new BaseResponseDTO<>("Success", "Stats fetched", stats);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        List<UserDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(
                new BaseResponseDTO<>(
                        "success",
                        "Users fetched successfully",
                        users
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/low-stock-dealers")   // <-- use dash version to match React
    public ResponseEntity<?> getLowStockDealers() {
        List<LowStockAdminDTO> list = productService.getLowStockForAdmin();
        return ResponseEntity.ok(new BaseResponseDTO<>("success", "Low stock dealers", list));
    }



}






