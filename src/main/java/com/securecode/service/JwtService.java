package com.securecode.service;

import com.securecode.security.UserPrincipal;
import java.util.Map;

public interface JwtService {
    String generateToken(UserPrincipal userPrincipal, Map<String, Object> extraClaims);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserPrincipal userPrincipal);
}
