package com.securecode.service;

import com.securecode.dto.AuthRequestDTO;
import com.securecode.dto.AuthResponseDTO;
import com.securecode.model.Tenant;
import com.securecode.model.User;
import com.securecode.model.enums.UserRole;
import com.securecode.security.UserPrincipal;
import com.securecode.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin_Success() {
        String email = "admin@securecode.com";
        String password = "password";
        AuthRequestDTO request = AuthRequestDTO.builder().email(email).password(password).build();

        Tenant tenant = Tenant.builder().id(UUID.randomUUID()).build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .role(UserRole.ADMIN)
                .tenant(tenant)
                .build();

        UserPrincipal principal = new UserPrincipal(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(eq(principal), any())).thenReturn("mockToken");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals(email, response.getEmail());
        assertEquals("ROLE_ADMIN", response.getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(eq(principal), any());
        verify(auditService).log(eq(user.getId()), eq("LOGIN"), eq("User"), eq(user.getId()), isNull());
    }
}
