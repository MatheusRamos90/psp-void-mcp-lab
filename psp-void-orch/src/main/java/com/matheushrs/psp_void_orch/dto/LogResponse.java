package com.matheushrs.psp_void_orch.dto;

import java.time.Instant;

public record LogResponse(String id, String trace, String origin, Instant createdAt) {
}
