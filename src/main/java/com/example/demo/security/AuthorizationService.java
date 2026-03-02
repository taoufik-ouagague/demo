package com.example.demo.security;

import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public boolean hasPermission(String username, String permission) {
        // Implementation for permission checking
        return true;
    }

    public boolean hasRole(String username, String role) {
        // Implementation for role checking
        return true;
    }
}
