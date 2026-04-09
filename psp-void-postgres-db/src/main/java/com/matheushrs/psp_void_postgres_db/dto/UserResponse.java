package com.matheushrs.psp_void_postgres_db.dto;

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
