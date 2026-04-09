package com.matheushrs.psp_void_postgres_db.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleResponse(UUID id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
