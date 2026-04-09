package com.matheushrs.psp_void_core.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPriceRequest(UUID productId, BigDecimal value, BigDecimal discountPercent) {
}
