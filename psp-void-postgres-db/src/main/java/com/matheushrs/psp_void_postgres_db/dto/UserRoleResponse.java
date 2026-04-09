package com.matheushrs.psp_void_postgres_db.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRoleResponse(UUID id, UUID userId, UUID roleId, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
