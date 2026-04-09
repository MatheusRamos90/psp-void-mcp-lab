package com.matheushrs.psp_void_elasticsearch_db.dto;

import java.time.Instant;

public record LogResponse(String id, String trace, String origin, Instant createdAt) {
}
