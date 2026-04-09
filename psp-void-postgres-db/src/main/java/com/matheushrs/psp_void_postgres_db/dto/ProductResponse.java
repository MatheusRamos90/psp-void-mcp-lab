package com.matheushrs.psp_void_postgres_db.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
