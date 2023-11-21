package com.ecommerce.catalog_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CatalogRepository extends JpaRepository<Catalog, Integer> {
}
