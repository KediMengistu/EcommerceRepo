package com.ecommerce.auction_service.Client;

import com.ecommerce.auction_service.OutgoingRequestObjectBodies.CatalogAndAuctionRequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${application.config.payment-url}")
public interface PaymentClient {

    @PostMapping("/load")
    void loadPayInfoFromAuctionEnd(@RequestBody CatalogAndAuctionRequestBody catauction);
}
