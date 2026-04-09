package com.matheushrs.psp_void_orch.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductPriceResponse(
        UUID id,
        UUID productId,
        BigDecimal value,
        BigDecimal discountPercent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
