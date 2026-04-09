package com.matheushrs.psp_void_postgres_db.service;

import com.matheushrs.psp_void_postgres_db.dto.ProductRequest;
import com.matheushrs.psp_void_postgres_db.dto.ProductResponse;
import com.matheushrs.psp_void_postgres_db.entity.ProductEntity;
import com.matheushrs.psp_void_postgres_db.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse create(ProductRequest request) {
        var entity = ProductEntity.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status() != null ? request.status() : true)
                .build();
        return toResponse(repository.save(entity));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
    }

    public ProductResponse update(UUID id, ProductRequest request) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + id));
        entity.setName(request.name());
        entity.setDescription(request.description());
        if (request.status() != null) entity.setStatus(request.status());
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        repository.deleteById(id);
    }

    private ProductResponse toResponse(ProductEntity e) {
        return new ProductResponse(e.getId(), e.getName(), e.getDescription(),
                e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
