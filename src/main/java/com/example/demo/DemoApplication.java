package com.example.demo;

import com.example.demo.entity.RoleType;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Create Admin Only Once
    @Bean
    public CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            String adminEmail = "piyushnare77@gmail.com";

            if (!repo.existsByEmail(adminEmail)) {

                User admin = new User();
                admin.setName("Piyush");
                admin.setEmail(adminEmail);

                // Set Password
                admin.setPassword(encoder.encode("Admin@123"));

                admin.setRole(RoleType.ADMIN);
                admin.setMobileNo("9766431874");
                admin.setAddress("HQ Office");
                admin.setStatus(true);

                repo.save(admin);

                System.out.println("✔ Admin created successfully!");
            } else {
                System.out.println("✔ Admin already exists — no new admin created.");
            }
        };
    }
}
