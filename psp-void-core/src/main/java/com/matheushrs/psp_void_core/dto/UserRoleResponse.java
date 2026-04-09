package com.matheushrs.psp_void_core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRoleResponse(UUID id, UUID userId, UUID roleId, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
