package com.matheushrs.psp_void_postgres_db.service;

import com.matheushrs.psp_void_postgres_db.dto.ProductPriceRequest;
import com.matheushrs.psp_void_postgres_db.dto.ProductPriceResponse;
import com.matheushrs.psp_void_postgres_db.entity.ProductPriceEntity;
import com.matheushrs.psp_void_postgres_db.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductPriceService {

    private final ProductPriceRepository repository;

    public ProductPriceResponse create(ProductPriceRequest request) {
        var entity = ProductPriceEntity.builder()
                .productId(request.productId())
                .value(request.value())
                .discountPercent(request.discountPercent())
                .build();
        return toResponse(repository.save(entity));
    }

    public List<ProductPriceResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductPriceResponse findById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NoSuchElementException("ProductPrice not found: " + id));
    }

    public List<ProductPriceResponse> findByProductId(UUID productId) {
        return repository.findByProductId(productId).stream().map(this::toResponse).toList();
    }

    public ProductPriceResponse update(UUID id, ProductPriceRequest request) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ProductPrice not found: " + id));
        entity.setValue(request.value());
        if (request.discountPercent() != null) entity.setDiscountPercent(request.discountPercent());
        return toResponse(repository.save(entity));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("ProductPrice not found: " + id);
        }
        repository.deleteById(id);
    }

    private ProductPriceResponse toResponse(ProductPriceEntity e) {
        return new ProductPriceResponse(e.getId(), e.getProductId(), e.getValue(),
                e.getDiscountPercent(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
