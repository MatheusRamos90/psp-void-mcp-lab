package com.matheushrs.psp_void_orch.controller;

import com.matheushrs.psp_void_orch.client.CoreApiClient;
import com.matheushrs.psp_void_orch.dto.ProductRequest;
import com.matheushrs.psp_void_orch.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management operations")
public class ProductController {

    private final CoreApiClient coreClient;

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponse> create(
            @RequestBody ProductRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coreClient.createProduct(request, origin));
    }

    @GetMapping
    @Operation(summary = "List all products")
    public List<ProductResponse> findAll() {
        return coreClient.findAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ProductResponse findById(@PathVariable UUID id) {
        return coreClient.findProductById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ProductResponse update(
            @PathVariable UUID id,
            @RequestBody ProductRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return coreClient.updateProduct(id, request, origin);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        coreClient.deleteProduct(id, origin);
        return ResponseEntity.noContent().build();
    }
}
