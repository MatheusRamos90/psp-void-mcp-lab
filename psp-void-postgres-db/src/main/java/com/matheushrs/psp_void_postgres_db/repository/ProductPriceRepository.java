package com.matheushrs.psp_void_postgres_db.repository;

import com.matheushrs.psp_void_postgres_db.entity.ProductPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductPriceRepository extends JpaRepository<ProductPriceEntity, UUID> {

    List<ProductPriceEntity> findByProductId(UUID productId);
}
