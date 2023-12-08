package com.ecommerce.auction_service.Client;

import com.ecommerce.auction_service.OtherServiceObjects.Catalog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "catalog-service", url = "${application.config.catalog-url}")
public interface CatalogClient {

    @GetMapping("/searchById")
    @ResponseBody
    Catalog searchCatalogById(@RequestParam int id);

    @PutMapping("/setAsExpired")
    void setCatalogAsExpired(@RequestParam int id);

    @DeleteMapping("/deleteitem")
    void removeFromCatalogById(@RequestParam int id);
}
