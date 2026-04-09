package com.matheushrs.psp_void_core.service;

import com.matheushrs.psp_void_core.client.ElasticsearchDbClient;
import com.matheushrs.psp_void_core.client.PostgresDbClient;
import com.matheushrs.psp_void_core.dto.ProductRequest;
import com.matheushrs.psp_void_core.dto.ProductResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final PostgresDbClient postgresDb;
    private final ElasticsearchDbClient elasticsearchDb;

    public ProductResponse create(ProductRequest request, Origin origin) {
        var product = postgresDb.createProduct(request);
        elasticsearchDb.saveLog("product created: " + product.id(), origin);
        return product;
    }

    public List<ProductResponse> findAll() {
        return postgresDb.findAllProducts();
    }

    public ProductResponse findById(UUID id) {
        return postgresDb.findProductById(id);
    }

    public ProductResponse update(UUID id, ProductRequest request, Origin origin) {
        var product = postgresDb.updateProduct(id, request);
        elasticsearchDb.saveLog("product updated: " + id, origin);
        return product;
    }

    public void delete(UUID id, Origin origin) {
        postgresDb.deleteProduct(id);
        elasticsearchDb.saveLog("product deleted: " + id, origin);
    }
}
