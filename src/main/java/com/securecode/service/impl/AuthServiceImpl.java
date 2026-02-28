package com.securecode.service.impl;

import com.securecode.dto.AuthRequestDTO;
import com.securecode.dto.AuthResponseDTO;
import com.securecode.security.UserPrincipal;
import com.securecode.service.AuthService;
import com.securecode.service.AuditService;
import com.securecode.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal, Map.of(
                "role", principal.getAuthorities().iterator().next().getAuthority(),
                "tenantId", principal.getTenantId()));

        auditService.log(principal.getId(), "LOGIN", "User", principal.getId(), null);

        return AuthResponseDTO.builder()
                .token(token)
                .email(principal.getEmail())
                .role(principal.getAuthorities().iterator().next().getAuthority())
                .build();
    }
}
