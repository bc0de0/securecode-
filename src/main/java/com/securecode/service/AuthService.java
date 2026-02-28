package com.securecode.service;

import com.securecode.dto.AuthRequestDTO;
import com.securecode.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
}
