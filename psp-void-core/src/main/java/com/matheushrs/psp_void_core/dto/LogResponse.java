package com.matheushrs.psp_void_core.dto;

import java.time.Instant;

public record LogResponse(String id, String trace, String origin, Instant createdAt) {
}
