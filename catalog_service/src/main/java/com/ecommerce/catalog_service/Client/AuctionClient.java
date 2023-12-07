package com.ecommerce.catalog_service.Client;

import com.ecommerce.catalog_service.OutgoingRequestObjectBodies.CatalogAndTimeRequestBody;
import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auction-service", url = "${application.config.auction-url}")
public interface AuctionClient {

    @PostMapping("/createauction")
    boolean createAuctionFromCatTimeItem(@RequestBody CatalogAndTimeRequestBody catandtime);

    @GetMapping("/getAuctionFromCatalog")
    Auction getAuctionFromCatId(@RequestParam int auctioneditemid);
}