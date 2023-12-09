package com.ecommerce.auction_service;

import com.ecommerce.auction_service.Bid.Bid;
import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/id_auction")
    public Auction getAuctionFromId(@RequestParam int id){
        return auctionService.getAuctionFromId(id);
    }

    @GetMapping("/id_auction_expired")
    public Auction getAuctionFromIdExpired(@RequestParam int id){
        return auctionService.getAuctionFromIdExpired(id);
    }

    @GetMapping("/validexpiredauction")
    public ResponseEntity<String> isValidExpired(@RequestParam int id){
        return ResponseEntity.ok(auctionService.isValidExpired(id));
    }

    @GetMapping("/allauctions")
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    @DeleteMapping("/removeauction")
    public void deleteAuction(@RequestParam int auctionid){
        auctionService.deleteAuctionAndCat(auctionid);
    }

    @GetMapping("/getAuctionFromCatalog")
    public Auction getAuctionFromCatId(@RequestParam int auctioneditemid){
        return auctionService.getAuctionFromCatId(auctioneditemid);
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