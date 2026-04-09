package com.matheushrs.psp_void_postgres_db.controller;

import com.matheushrs.psp_void_postgres_db.dto.ProductPriceRequest;
import com.matheushrs.psp_void_postgres_db.dto.ProductPriceResponse;
import com.matheushrs.psp_void_postgres_db.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class ProductPriceController {

    private final ProductPriceService service;

    @PostMapping
    public ResponseEntity<ProductPriceResponse> create(@RequestBody ProductPriceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public List<ProductPriceResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductPriceResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/by-product/{productId}")
    public List<ProductPriceResponse> findByProductId(@PathVariable UUID productId) {
        return service.findByProductId(productId);
    }

    @PutMapping("/{id}")
    public ProductPriceResponse update(@PathVariable UUID id, @RequestBody ProductPriceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
