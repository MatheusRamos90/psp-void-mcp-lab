package com.matheushrs.psp_void_orch.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoleResponse(UUID id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
