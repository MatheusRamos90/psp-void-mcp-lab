package com.matheushrs.psp_void_core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
