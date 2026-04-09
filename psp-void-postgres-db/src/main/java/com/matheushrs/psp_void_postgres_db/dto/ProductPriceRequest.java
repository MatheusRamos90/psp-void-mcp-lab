package com.matheushrs.psp_void_postgres_db.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPriceRequest(UUID productId, BigDecimal value, BigDecimal discountPercent) {
}
