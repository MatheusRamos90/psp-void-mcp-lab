package com.matheushrs.psp_void_orch.controller;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.ProductPriceRequest;
import com.matheushrs.psp_void_orch.dto.ProductPriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Product price management — write operations require ADMIN authority")
@SecurityRequirement(name = "bearerAuth")
public class ProductPriceController {

    private final CoreApiClient coreClient;

    // ── Read — any authenticated user ──────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all prices")
    public List<ProductPriceResponse> findAll() {
        return coreClient.findAllPrices();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get price by ID")
    public ProductPriceResponse findById(@PathVariable UUID id) {
        return coreClient.findPriceById(id);
    }

    // ── Write — ADMIN only ─────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new price for a product  [ADMIN]")
    public ResponseEntity<ProductPriceResponse> create(
            @RequestBody ProductPriceRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreClient.createPrice(request, origin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a price  [ADMIN]")
    public ProductPriceResponse update(
            @PathVariable UUID id,
            @RequestBody ProductPriceRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return coreClient.updatePrice(id, request, origin);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a price  [ADMIN]")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        coreClient.deletePrice(id, origin);
        return ResponseEntity.noContent().build();
    }
}
