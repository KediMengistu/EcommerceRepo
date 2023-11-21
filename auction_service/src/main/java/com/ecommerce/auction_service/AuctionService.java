package com.ecommerce.auction_service;

import com.ecommerce.auction_service.Client.CatalogClient;
import com.ecommerce.auction_service.Client.PaymentClient;
import com.ecommerce.auction_service.Client.UserClient;
import com.ecommerce.auction_service.OtherServiceObjects.Catalog;
import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import com.ecommerce.auction_service.OtherServiceObjects.User;
import com.ecommerce.auction_service.OutgoingRequestObjectBodies.CatalogAndAuctionRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserClient userclient;
    private final CatalogClient catalogclient;
    private final PaymentClient paymentclient;


    @Autowired
    public AuctionService(AuctionRepository auctionRepository, UserClient userclient, CatalogClient catalogclient, PaymentClient paymentclient) {
        this.auctionRepository = auctionRepository;
        this.userclient = userclient;
        this.catalogclient = catalogclient;
        this.paymentclient = paymentclient;
    }

    //creates the auction - never called by request from user - only internal calls from
    //catalog
    public boolean createAuction(CatalogAndTimeRequestBody catandtime) {
        //local fields
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
            auction.setStartprice(Math.round(catitem.getStartprice()*100.0)/100.0);
        }
        //it is dutch so 25% higher start price.
        else if(auction.getAuctiontype().equals("Dutch")){
            auction.setStartprice(Math.round((catitem.getStartprice() * 1.25)*100.0)/100.0);
        }
        auction.setStartdate(startdate);
        auction.setStarttime(starttime);
        auction.setDuration(catitem.getDuration());
        auction.setEndtime(endtime);
        auction.setEnddate(catitem.getEnddate());
        auctionRepository.save(auction);
        return true;
    }

    //returns a list of all the auctions that are currently up on the system.
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    //putting in bid for auctions.
    public boolean bid(String bidderusername, int auctioneditemid, double bid) {
        //local fields.
        User bidder;
        Optional<Auction> opAuction;
        Auction auction;
        Catalog catitem;
        CatalogAndAuctionRequestBody catauction;

        //extract bidder via unique username. #
        bidder = userclient.findBidderFromUsername(bidderusername);
        //bidder is not user of system - invalid bid.
        if(bidder==null){
            return false;
        }
        //bidder is not user of system.
        else{
            //extract auction for the auction that has the item with auctioneditemid.
            //one auction per item so it must be unique.
            opAuction = auctionRepository.findByauctioneditemid(auctioneditemid);

            //auction no longer up or never existed.
            if(opAuction.isEmpty()){
                return false;
            }
            //auction is up and does exist currently.
            else{
                //set auction placeholder.
                auction = opAuction.get();

                //extract catalog item that corresponds to auction that the bid wants to enter.
                catitem = catalogclient.searchCatalogById(auction.getAuctioneditemid());

                //check if bidder is not the same as seller of catalog item on auction.
                //true means invalid.
                if(bidder.getUserid()==catitem.getSellerid()){
                    return false;
                }
                //bidder is not seller can proceed with further bid validation.
                else{
                    //check if time of auction has run out - cannot bid so the highest bidder is loaded.
                    if (auction.getEnddate().isBefore(LocalDate.now()) || (auction.getEnddate().isEqual(LocalDate.now())
                        && auction.getEndtime().isBefore(LocalTime.now()))) {
                        //load to payment.
                        //catitem holds info about auctioned off item.
                        //auction holds info about the recently completed auction.
                        catauction = new CatalogAndAuctionRequestBody(catitem, auction);
                        paymentclient.loadPayInfoFromAuctionEnd(catauction);

                        //remove from catalog.
                        catalogclient.removeFromCatalogById(catitem.getItemid());

                        //remove from auction.
                        auctionRepository.delete(auction);
                        return false;
                    }
                    //time has not run out - further bid validation.
                    else{
                        //rounding bid to 2 decimal places
                        bid = Math.round(bid*100.0)/100.0;

                        //check the type of bid - forward or dutch.
                        //forward bid.
                        if(auction.getAuctiontype().equals("Forward")){
                            //check to see if bid is larger than highest bid.
                            if(bid>auction.getHighestbid()){
                                //update auction information.
                                auction.setHighestbid(bid);
                                auction.setHighestbidderid(bidder.getUserid());
                                auctionRepository.save(auction);
                                return true;
                            }
                            //invalid forward bid.
                            else{
                                return false;
                            }
                        }
                        //dutch bid.
                        else{
                            //valid dutch bid; threshold < bid <= startprice.
                            //we have set 0 as the threshold value.
                            if(bid>0 && bid<=auction.getStartprice()){
                                //update auction information
                                auction.setHighestbid(bid);
                                auction.setHighestbidderid(bidder.getUserid());
                                auctionRepository.save(auction);

                                //load to payment.
                                catauction = new CatalogAndAuctionRequestBody(catitem, auction);
                                paymentclient.loadPayInfoFromAuctionEnd(catauction);

                                //remove from catalog.
                                catalogclient.removeFromCatalogById(catitem.getItemid());

                                //remove from auction.
                                auctionRepository.delete(auction);
                                return true;
                            }
                            //invalid dutch bid.
                            else {
                                return false;
                            }
                        }
                    }
                }
            }
        }
    }
}