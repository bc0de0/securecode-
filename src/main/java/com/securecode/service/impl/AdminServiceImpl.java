package com.securecode.service.impl;

import com.securecode.model.Tenant;
import com.securecode.model.User;
import com.securecode.model.enums.UserRole;
import com.securecode.repository.TenantRepository;
import com.securecode.repository.UserRepository;
import com.securecode.service.AdminService;
import com.securecode.service.AuditService;
import com.securecode.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional
    public void createUser(String email, String password, UserRole role) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        UUID adminId = SecurityUtils.getCurrentUserId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .tenant(tenant)
                .build();

        user = userRepository.save(user);

        auditService.log(adminId, "USER_CREATION", "User", user.getId(), null);
    }

    @Override
    @Transactional
    public void updateUserRole(UUID userId, UserRole role) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        UUID adminId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found in tenant"));

        user.setRole(role);
        userRepository.save(user);

        auditService.log(adminId, "ROLE_CHANGE", "User", user.getId(), null);
    }
}
