package com.ecommerce.auction_service;

import com.ecommerce.auction_service.OtherServiceObjects.Catalog;
import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public boolean createAuction(CatalogAndTimeRequestBody catandtime) {
        //localFeilds
        //the auction to be saved in auction table.
        Auction auction = new Auction();

        //these local fields are used to
        //retreive the catalog item and auction time
        //parameters to set new auction.
        Catalog catitem = catandtime.getCatalog();
        LocalTime starttime = catandtime.getStarttime();
        LocalDate startdate = catandtime.getStartDate();
        LocalTime endtime = catandtime.getEndtime();

        //setting up the auction
        auction.setAuctioneditemid(catitem.getItemid());
        auction.setAuctiontype(catitem.getAuctiontype());

        //startprice is different for different auction types.
        //forward = original startprice for corresponding catalog item.
        //dutch = 0.25% higher than the original startprice for corresponding catalog item.
        if(auction.getAuctiontype().equals("Forward")){
            auction.setStartprice(catitem.getStartprice());
        }
        else if(auction.getAuctiontype().equals("Dutch")){
            auction.setStartprice(catitem.getStartprice() * 1.25);
        }
        auction.setStartdate(startdate);
        auction.setStarttime(starttime);
        auction.setDuration(catitem.getDuration());
        auction.setEndtime(endtime);
        auction.setEnddate(catitem.getEnddate());
        return true;
    }
}
