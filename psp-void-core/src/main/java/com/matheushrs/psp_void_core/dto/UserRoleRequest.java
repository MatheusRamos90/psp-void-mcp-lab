package com.matheushrs.psp_void_core.dto;

import java.util.UUID;

public record UserRoleRequest(UUID userId, UUID roleId) {
}
