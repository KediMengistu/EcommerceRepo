package com.ecommerce.auction_service;

import com.ecommerce.auction_service.Bid.Bid;
import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/allauctions")
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    @DeleteMapping("/removeauction")
    public void deleteAuction(@RequestParam int auctionid){
        auctionService.deleteAuction(auctionid);
    }

    @PostMapping("/bid")
    public boolean bid(@RequestParam String bidderusername,
                       @RequestParam int auctioneditemid,
                       @RequestParam double bid) {
        return auctionService.bid(bidderusername, auctioneditemid, bid);
    }

    @GetMapping("/allbids")
    public List<Bid> getAllBids(){
        return auctionService.getAllBids();
    }

    @GetMapping("/id_allbids")
    public List<Bid> getAllBidsById(@RequestParam int auctionid){
        return auctionService.getAllBidsById(auctionid);
    }
}