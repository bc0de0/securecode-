package com.securecode.service;

import com.securecode.model.Tenant;
import com.securecode.model.User;
import com.securecode.model.enums.UserRole;
import com.securecode.repository.TenantRepository;
import com.securecode.repository.UserRepository;
import com.securecode.service.impl.AdminServiceImpl;
import com.securecode.util.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private UUID tenantId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        adminId = UUID.randomUUID();
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(adminId);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void testCreateUser_Success() {
        String email = "test@example.com";
        String password = "securePassword123";
        UserRole role = UserRole.ANALYST;

        Tenant tenant = Tenant.builder().id(tenantId).build();
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        User savedUser = User.builder().id(UUID.randomUUID()).email(email).build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        adminService.createUser(email, password, role);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(password);
        verify(auditService).log(eq(adminId), eq("USER_CREATION"), eq("User"), any(), any());
    }

    @Test
    void testUpdateUserRole_Success() {
        UUID userId = UUID.randomUUID();
        UserRole newRole = UserRole.ADMIN;

        User user = User.builder()
                .id(userId)
                .tenant(Tenant.builder().id(tenantId).build())
                .role(UserRole.USER)
                .build();

        when(userRepository.findByIdAndTenantId(userId, tenantId)).thenReturn(Optional.of(user));

        adminService.updateUserRole(userId, newRole);

        assertEquals(newRole, user.getRole());
        verify(userRepository).save(user);
        verify(auditService).log(eq(adminId), eq("ROLE_CHANGE"), eq("User"), eq(userId), any());
    }
}
