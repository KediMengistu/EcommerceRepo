package com.ecommerce.catalog_service.Client;

import com.ecommerce.catalog_service.OutgoingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auction-service", url = "${application.config.auction-url}")
public interface AuctionClient {

    @PostMapping("/createauction")
    boolean createAuctionFromCatTimeItem(@RequestBody CatalogAndTimeRequestBody catandtime);
}