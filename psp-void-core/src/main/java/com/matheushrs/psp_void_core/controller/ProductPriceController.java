package com.matheushrs.psp_void_core.controller;

import com.matheushrs.psp_void_core.dto.ProductPriceRequest;
import com.matheushrs.psp_void_core.dto.ProductPriceResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import com.matheushrs.psp_void_core.service.ProductPriceService;
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
    public ResponseEntity<ProductPriceResponse> create(
            @RequestBody ProductPriceRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request, Origin.valueOf(origin)));
    }

    @GetMapping
    public List<ProductPriceResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductPriceResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProductPriceResponse update(
            @PathVariable UUID id,
            @RequestBody ProductPriceRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return service.update(id, request, Origin.valueOf(origin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        service.delete(id, Origin.valueOf(origin));
        return ResponseEntity.noContent().build();
    }
}
