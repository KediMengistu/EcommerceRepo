package com.ecommerce.payment_service.Client;

import com.ecommerce.payment_service.IncomingRequestObjectBodies.Bid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "auction-service", url = "${application.config.auction-url}")
public interface AuctionClient {

    @GetMapping("/allbids")
    public List<Bid> getallbids(@RequestParam int auctionid);

    @DeleteMapping("/removeauction")
    public void deleteAuction(@RequestParam int auctionid);
}
