package com.securecode.controller;

import com.securecode.model.enums.UserRole;
import com.securecode.service.AdminService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest request) {
        adminService.createUser(request.getEmail(), request.getPassword(), request.getRole());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(@PathVariable UUID userId, @RequestBody RoleUpdateRequest request) {
        adminService.updateUserRole(userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class UserCreateRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8)
        private String password;

        @NotNull
        private UserRole role;
    }

    @Data
    public static class RoleUpdateRequest {
        @NotNull
        private UserRole role;
    }
}
