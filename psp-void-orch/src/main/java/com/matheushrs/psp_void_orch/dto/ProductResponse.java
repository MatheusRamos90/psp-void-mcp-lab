package com.matheushrs.psp_void_orch.dto;

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
