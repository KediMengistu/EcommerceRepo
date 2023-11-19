package com.ecommerce.auction_service.Client;

import com.ecommerce.auction_service.OtherServiceObjects.Catalog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "catalog-service", url = "${application.config.catalog-url}")
public interface CatalogClient {

    @GetMapping("/searchById")
    @ResponseBody
    Catalog searchCatalogById(@RequestParam int id);

    @DeleteMapping("/deleteitem")
    void removeFromCatalogById(@RequestParam int id);
}
