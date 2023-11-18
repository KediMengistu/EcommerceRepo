package com.ecommerce.auction_service;

import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "ecommerce/auction")
public class AuctionController {
    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService){
        this.auctionService = auctionService;
    }

    @PostMapping("/createauction")
    public boolean createAuction(@RequestBody CatalogAndTimeRequestBody catandtime) {
        return auctionService.createAuction(catandtime);
    }
}