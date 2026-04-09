package com.matheushrs.psp_void_orch.dto;

import java.util.List;
import java.util.UUID;

public record TokenResponse(
        String token,
        String type,
        long expiresIn,
        UUID userId,
        String email,
        List<String> roles
) {
    public TokenResponse(String token, long expiresIn, UUID userId, String email, List<String> roles) {
        this(token, "Bearer", expiresIn, userId, email, roles);
    }
}
