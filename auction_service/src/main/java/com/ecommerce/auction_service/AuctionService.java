package com.ecommerce.auction_service;

import com.ecommerce.auction_service.Client.CatalogClient;
import com.ecommerce.auction_service.Client.PaymentClient;
import com.ecommerce.auction_service.Client.UserClient;
import com.ecommerce.auction_service.OtherServiceObjects.Catalog;
import com.ecommerce.auction_service.IncomingRequestObjectBodies.CatalogAndTimeRequestBody;
import com.ecommerce.auction_service.OtherServiceObjects.User;
import com.ecommerce.auction_service.OutgoingRequestObjectBodies.CatalogAndAuctionRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
            findProperDecrementedTimeAndValue(auction);
        }
        auction.setStartdate(startdate);
        auction.setStarttime(starttime);
        auction.setDuration(catitem.getDuration());
        auction.setEndtime(endtime);
        auction.setEnddate(catitem.getEnddate());
        auctionRepository.save(auction);

        //notes on scheduler:
        //we have saved the auction for the corresponding catalog item that was just made.
        //now we need to check if the new auction is the earliest ending auction.
        //save the recently created auction to global "newauction" variable.
        //scheduler will then be able to have access to this newauction item.
        //scheduler should check to see if this auction finishes eariler that the current earliest.
        //if it does then, swap between the two.
        //if it does not then do nothing - the scheduler will continue to check the original.
        //alternative to having the date and time issue would maybe be to just get all the auctions
        //as opposed to all of their end date and time values and then check the
        //local date and time fields for each auction object
        //ex: auctionrepository.findAll()
        //iterate through the returned list, checking for earliest ending one
        //and then storing that as the one that the scheduler will check.
        //*****Important note*****
        //for bid and payment to work you need to make it so that when the catalog item is deleted
        //so is its corresponding existing auction - I can explain to you when you have time or at
        //the meeting.
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

        //extract bidder via unique username.
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
                    //check if it is dutch auction whose value has been decremented.
                    if (auction.getEnddate().isBefore(LocalDate.now()) || (auction.getEnddate().isEqual(LocalDate.now())
                        && auction.getEndtime().isBefore(LocalTime.now())) || (auction.getAuctiontype().equals("Dutch")
                        && auction.getStartprice()<=0)) {
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

    private void findProperDecrementedTimeAndValue(Auction auction) {
        //local fields.
        LocalTime duration;
        double startprice;
        int totalseconds;
        double numberofdecrements;

        //local fields used to initialize parameters for auction.
        LocalTime nextdecrementtime;
        LocalTime decrementinveral;
        double decrementvalue;

        //setting up values.
        duration = auction.getDuration();
        startprice = auction.getStartprice();

        //convert total duration into seconds.
        totalseconds = duration.getHour() * 3600 + duration.getMinute() * 60 + duration.getSecond();

        //1min <= duration <= 5 min - decrement every 30 seconds
        if (totalseconds >= 60 && totalseconds <= 5 * 60) {
            numberofdecrements = totalseconds/30;
            decrementvalue = startprice/numberofdecrements;
            decrementvalue = Math.round((decrementvalue * 100.0)/100.0);
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinveral = LocalTime.of(0, 0, 30);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinveral.getHour())
                                .plusMinutes(decrementinveral.getMinute())
                                .plusSeconds(decrementinveral.getSecond());
            auction.setDecrementinvterval(decrementinveral);
            auction.setDecrementvalue(decrementvalue);
        }

        //5min < duration <= 10 min - decrement every 1 min
        else if (totalseconds > 5 * 60 && totalseconds <= 10 * 60) {
            numberofdecrements = totalseconds/60;
            decrementvalue = startprice/numberofdecrements;
            decrementvalue = Math.round((decrementvalue * 100.0)/100.0);
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinveral = LocalTime.of(0, 1, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinveral.getHour())
                                .plusMinutes(decrementinveral.getMinute())
                                .plusSeconds(decrementinveral.getSecond());
            auction.setDecrementinvterval(decrementinveral);
            auction.setDecrementvalue(decrementvalue);
        }

        //10min < duration <= 30 min - decrement every 5 min
        else if (totalseconds > 10 * 60 && totalseconds <= 30 * 60) {
            numberofdecrements = totalseconds/300;
            decrementvalue = startprice/numberofdecrements;
            decrementvalue = Math.round((decrementvalue * 100.0)/100.0);
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinveral = LocalTime.of(0, 5, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinveral.getHour())
                                .plusMinutes(decrementinveral.getMinute())
                                .plusSeconds(decrementinveral.getSecond());
            auction.setDecrementinvterval(decrementinveral);
            auction.setDecrementvalue(decrementvalue);
        }

        //30min < duration <= 60 min - decrement every 15 min
        else if (totalseconds > 30 * 60 && totalseconds <= 60 * 60) {
            numberofdecrements = totalseconds/(15*60);
            decrementvalue = startprice/numberofdecrements;
            decrementvalue = Math.round((decrementvalue * 100.0)/100.0);
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinveral = LocalTime.of(0, 15, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinveral.getHour())
                                .plusMinutes(decrementinveral.getMinute())
                                .plusSeconds(decrementinveral.getSecond());
            auction.setDecrementinvterval(decrementinveral);
            auction.setDecrementvalue(decrementvalue);
        }
        //duration > 60 min - decrement every 30 min
        else if(totalseconds < 60 * 60){
            numberofdecrements = totalseconds/(30*60);
            decrementvalue = startprice/numberofdecrements;
            decrementvalue = Math.round((decrementvalue * 100.0)/100.0);
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinveral = LocalTime.of(0, 30, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinveral.getHour())
                                .plusMinutes(decrementinveral.getMinute())
                                .plusSeconds(decrementinveral.getSecond());
            auction.setDecrementinvterval(decrementinveral);
            auction.setDecrementvalue(decrementvalue);
        }
    }

    @Scheduled(fixedRate = 1000)
    private void dutchAuctionDecremented(){
        //local field.
        List<Auction> auctionlist;
        Catalog catitem;
        CatalogAndAuctionRequestBody catauction;
        LocalTime duraction;

        //intialize list
        auctionlist = auctionRepository.findAll();

        //going through the entire auction list so the list is required.
        for(Auction auction: auctionlist){
            duraction = auction.getDuration();

        }
    }
}