package com.securecode.service;

import com.securecode.model.enums.UserRole;
import java.util.UUID;

public interface AdminService {
    void createUser(String email, String password, UserRole role);

    void updateUserRole(UUID userId, UserRole role);
}
