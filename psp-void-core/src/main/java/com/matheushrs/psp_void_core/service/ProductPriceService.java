package com.matheushrs.psp_void_core.service;

import com.matheushrs.psp_void_core.client.ElasticsearchDbClient;
import com.matheushrs.psp_void_core.client.PostgresDbClient;
import com.matheushrs.psp_void_core.dto.ProductPriceRequest;
import com.matheushrs.psp_void_core.dto.ProductPriceResponse;
import com.matheushrs.psp_void_core.enums.Origin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductPriceService {

    private final PostgresDbClient postgresDb;
    private final ElasticsearchDbClient elasticsearchDb;

    public ProductPriceResponse create(ProductPriceRequest request, Origin origin) {
        var price = postgresDb.createPrice(request);
        elasticsearchDb.saveLog("price created: " + price.id(), origin);
        return price;
    }

    public List<ProductPriceResponse> findAll() {
        return postgresDb.findAllPrices();
    }

    public ProductPriceResponse findById(UUID id) {
        return postgresDb.findPriceById(id);
    }

    public ProductPriceResponse update(UUID id, ProductPriceRequest request, Origin origin) {
        var price = postgresDb.updatePrice(id, request);
        elasticsearchDb.saveLog("price updated: " + id, origin);
        return price;
    }

    public void delete(UUID id, Origin origin) {
        postgresDb.deletePrice(id);
        elasticsearchDb.saveLog("price deleted: " + id, origin);
    }
}
