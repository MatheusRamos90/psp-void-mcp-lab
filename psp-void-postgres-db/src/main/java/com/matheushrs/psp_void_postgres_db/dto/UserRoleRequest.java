package com.matheushrs.psp_void_postgres_db.dto;

import java.util.UUID;

public record UserRoleRequest(UUID userId, UUID roleId) {
}
