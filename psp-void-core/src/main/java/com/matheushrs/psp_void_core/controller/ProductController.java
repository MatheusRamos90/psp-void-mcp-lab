package com.matheushrs.psp_void_core.controller;

import com.matheushrs.psp_void_core.dto.ProductRequest;
import com.matheushrs.psp_void_core.dto.ProductResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import com.matheushrs.psp_void_core.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestBody ProductRequest request,
            @RequestHeader(value = "X-Origin", defaultValue = "WEB") String origin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request, Origin.valueOf(origin)));
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable UUID id,
            @RequestBody ProductRequest request,
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
