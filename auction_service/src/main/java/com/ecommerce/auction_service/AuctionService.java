package com.ecommerce.auction_service;

import com.ecommerce.auction_service.Bid.Bid;
import com.ecommerce.auction_service.Bid.BidRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserClient userclient;
    private final CatalogClient catalogclient;
    private final PaymentClient paymentclient;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, BidRepository bidRepository, UserClient userclient, CatalogClient catalogclient, PaymentClient paymentclient) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userclient = userclient;
        this.catalogclient = catalogclient;
        this.paymentclient = paymentclient;
    }

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
        if(auction.getAuctiontype().equals("Forward")){
            auction.setStartprice(Math.round(catitem.getStartprice()*100.0)/100.0);
            auction.setStartdate(startdate);
            auction.setStarttime(starttime);
            auction.setDuration(catitem.getDuration());
            auction.setEndtime(endtime);
            auction.setEnddate(catitem.getEnddate());
            auction.setExpired(false);
            auctionRepository.save(auction);
        }
        //dutch so the startprice is elevated.
        else if(auction.getAuctiontype().equals("Dutch")){
            auction.setStartprice(setupDutchStartPrice(catitem.getStartprice()));
            auction.setStartdate(startdate);
            auction.setStarttime(starttime);
            auction.setDuration(catitem.getDuration());
            auction.setEndtime(endtime);
            auction.setEnddate(catitem.getEnddate());
            setupDecrementvaluesAndFinal(auction);
            auction.setExpired(false);
            auctionRepository.save(auction);
        }
        return true;
    }

    //returns a list of all the auctions that are currently up on the system.
    public List<Auction> getAllAuctions() {
        List<Auction> result = new ArrayList<>();
        List<Auction> auctionList = auctionRepository.findAll();
        for(int i=0; i<auctionList.size(); i++){
            if(auctionList.get(i)!=null && auctionList.get(i).isExpired()){
                result.add(auctionList.get(i));
            }
        }
        return result;
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
        if (bidder == null) {
            return false;
        }
        //bidder is user of system.
        else {
            //extract auction for the auction that has the item with auctioneditemid.
            //one auction per item so it must be unique.
            opAuction = auctionRepository.findByauctioneditemid(auctioneditemid);

            //auction no longer up or never existed.
            if (opAuction.isEmpty()) {
                return false;
            }
            //auction is up and does exist currently.
            else {
                //set auction placeholder.
                auction = opAuction.get();

                //the auction has not expired - bid can be made but needs to be further processed.
                if (auction.isExpired() == false) {

                    //extract catalog item that corresponds to auction that the bid wants to enter.
                    catitem = catalogclient.searchCatalogById(auction.getAuctioneditemid());

                    //check if bidder is not the same as seller of catalog item on auction.
                    //true means invalid.
                    if(bidder.getUserid()==catitem.getSellerid() ||
                      ((bidder.getAuctionid()!=0 &&
                       bidder.getAuctionid()!=auction.getAuctionid()))){
                        return false;
                    }
                    //bidder is not the seller and is either a new bidder or a returning bidder to this auction.
                    else {

                        //check if bid is positive.
                        //bid is negative.
                        if (bid <= 0) {
                            return false;
                        }
                        //bid is positive.
                        else {
                            //forward bid.
                            if (auction.getAuctiontype().equals("Forward")) {
                                //bid is the highest.
                                if (bid > auction.getHighestbid()) {
                                    //set the bidder to be in this auction if not already; it must be 0 at this point.
                                    if(bidder.getAuctionid()!=auction.getAuctionid()){
                                        userclient.setAuctionForBidder(bidder.getUsername(), auction.getAuctionid());
                                    }
                                    //update auction info
                                    auction.setHighestbid(bid);
                                    auction.setHighestbidderid(bidder.getUserid());
                                    //save bid in bid table.
                                    createAndStoreBid(bid, auction, catitem.getSellerid());
                                    //save updated auction into table.
                                    auctionRepository.save(auction);
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            //dutch bid.
                            else {
                                //bid made is equivalent to current price.
                                //remove item from catalog.
                                if (bid == auction.getStartprice()) {
                                    //set the bidder to be in this auction if not already; it must be 0 at this point.
                                    if(bidder.getAuctionid()!=auction.getAuctionid()){
                                        userclient.setAuctionForBidder(bidder.getUsername(), auction.getAuctionid());
                                    }
                                    //update auction info.
                                    auction.setHighestbid(bid);
                                    auction.setHighestbidderid(bidder.getUserid());
                                    auction.setExpired(true);
                                    //setting the corresponding catalog item to expire.
                                    catalogclient.setCatalogAsExpired(auction.getAuctioneditemid());
                                    //save bid in bid table.
                                    createAndStoreBid(bid, auction, catitem.getSellerid());
                                    //load receipt information.
                                    catauction = new CatalogAndAuctionRequestBody(catitem, auction);
                                    paymentclient.loadPayInfoFromAuctionEndReciept(catauction);
                                    //save updated auction into table.
                                    auctionRepository.save(auction);
                                    return true;
                                }
                                else {
                                    return false;
                                }
                            }
                        }
                    }
                }
                else {
                    return false;
                }
            }
        }
    }

    //sets up the dutch auctions startprice.
    private double setupDutchStartPrice(double startprice) {
        double finalprice = 0;
        if(0<startprice && startprice<=1){
            startprice = Math.round((startprice + 5)*100.0)/100.0;
        }
        else if(1<startprice && startprice<=5){
            startprice = Math.round((startprice + 10)*100.0)/100.0;
        }
        else if(5<startprice && startprice<=10){
            startprice = Math.round((startprice + 15)*100.0)/100.0;
        }
        else if(10<startprice && startprice<=50){
            startprice = Math.round((startprice + 20)*100.0)/100.0;
        }
        else if(50<startprice && startprice<=100){
            startprice = Math.round((startprice + 25)*100.0)/100.0;
        }
        else if(100>startprice){
            startprice = Math.round((startprice + 30)*100.0)/100.0;
        }
        return startprice;
    }

    //sets up the dutch auctions decrement and final values.
    private void setupDecrementvaluesAndFinal(Auction auction) {
        //local fields.
        LocalTime duration;
        double startprice;
        int totalseconds;
        int numberofdecrements;

        //local fields used to initialize parameters for auction.
        LocalTime nextdecrementtime = null;
        LocalDate nextdecrementdate = null;
        LocalTime decrementinterval = null;
        double decrementvalue = 0;
        double finalprice = 0;

        //setting up values.
        duration = auction.getDuration();
        startprice = auction.getStartprice();

        //convert total duration into seconds.
        totalseconds = duration.getHour() * 3600 + duration.getMinute() * 60 + duration.getSecond();

        //1min <= duration <= 5 min - decrement every 30 seconds
        if (totalseconds >= 60 && totalseconds <= 5 * 60) {
            //want to get to final value and leave it on for a single 30 second unit.
            totalseconds = totalseconds - 30;
            numberofdecrements = (int) Math.floor(totalseconds/30);
            decrementvalue = (startprice-(startprice*0.25))/numberofdecrements;
            decrementvalue = Math.round(decrementvalue * 100.0)/100.0;
            finalprice = Math.round(getfinal(startprice, numberofdecrements, decrementvalue)*100.0)/100.0;
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinterval = LocalTime.of(0, 0, 30);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinterval.getHour())
                    .plusMinutes(decrementinterval.getMinute())
                    .plusSeconds(decrementinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);

            if (nextdecrementtime.isBefore(LocalTime.now())) {
                // Time has rolled over to the next day
                nextdecrementdate = LocalDate.now().plusDays(1);
            }
            //checks to see if endtime does not go into next day
            else {
                nextdecrementdate = LocalDate.now();
            }
        }

        //5min < duration <= 10 min - decrement every 1 min
        else if (totalseconds > 5 * 60 && totalseconds <= 10 * 60) {
            totalseconds = totalseconds - 60;
            numberofdecrements = (int) Math.floor(totalseconds/60);
            decrementvalue = (startprice-(startprice*0.25))/numberofdecrements;
            decrementvalue = Math.round(decrementvalue * 100.0)/100.0;
            finalprice = Math.round(getfinal(startprice, numberofdecrements, decrementvalue)*100.0)/100.0;
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinterval = LocalTime.of(0, 1, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinterval.getHour())
                    .plusMinutes(decrementinterval.getMinute())
                    .plusSeconds(decrementinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);
            if (nextdecrementtime.isBefore(LocalTime.now())) {
                // Time has rolled over to the next day
                nextdecrementdate = LocalDate.now().plusDays(1);
            }
            //checks to see if endtime does not go into next day
            else {
                nextdecrementdate = LocalDate.now();
            }
        }

        //10min < duration <= 30 min - decrement every 5 min
        else if (totalseconds > 10 * 60 && totalseconds <= 30 * 60) {
            totalseconds = totalseconds - (5*60);
            numberofdecrements = (int) Math.floor(totalseconds/(5*60));
            decrementvalue = (startprice-(startprice*0.25))/numberofdecrements;
            decrementvalue = Math.round(decrementvalue * 100.0)/100.0;
            finalprice = Math.round(getfinal(startprice, numberofdecrements, decrementvalue)*100.0)/100.0;
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinterval = LocalTime.of(0, 5, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinterval.getHour())
                    .plusMinutes(decrementinterval.getMinute())
                    .plusSeconds(decrementinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);
            if (nextdecrementtime.isBefore(LocalTime.now())) {
                // Time has rolled over to the next day
                nextdecrementdate = LocalDate.now().plusDays(1);
            }
            //checks to see if endtime does not go into next day
            else {
                nextdecrementdate = LocalDate.now();
            }
        }

        //30min < duration <= 60 min - decrement every 15 min
        else if (totalseconds > 30 * 60 && totalseconds <= 60 * 60) {
            totalseconds = totalseconds - (15*60);
            numberofdecrements = (int) Math.floor(totalseconds/(15*60));
            decrementvalue = (startprice-(startprice*0.25))/numberofdecrements;
            decrementvalue = Math.round(decrementvalue * 100.0)/100.0;
            finalprice = Math.round(getfinal(startprice, numberofdecrements, decrementvalue)*100.0)/100.0;
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinterval = LocalTime.of(0, 15, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinterval.getHour())
                    .plusMinutes(decrementinterval.getMinute())
                    .plusSeconds(decrementinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);
            if (nextdecrementtime.isBefore(LocalTime.now())) {
                // Time has rolled over to the next day
                nextdecrementdate = LocalDate.now().plusDays(1);
            }
            //checks to see if endtime does not go into next day
            else {
                nextdecrementdate = LocalDate.now();
            }
        }
        //duration > 60 min - decrement every 30 min
        else if(totalseconds > 60 * 60){
            totalseconds = totalseconds - (30*60);
            numberofdecrements = (int) Math.floor(totalseconds/(30*60));
            decrementvalue = (startprice-(startprice*0.25))/numberofdecrements;
            decrementvalue = Math.round(decrementvalue * 100.0)/100.0;
            finalprice = Math.round(getfinal(startprice, numberofdecrements, decrementvalue)*100.0)/100.0;
            nextdecrementtime = auction.getStarttime();

            //setting up and saving dutch auction info
            decrementinterval = LocalTime.of(0, 30, 0);
            nextdecrementtime = nextdecrementtime.plusMinutes(decrementinterval.getHour())
                    .plusMinutes(decrementinterval.getMinute())
                    .plusSeconds(decrementinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);
            if (nextdecrementtime.isBefore(LocalTime.now())) {
                // Time has rolled over to the next day
                nextdecrementdate = LocalDate.now().plusDays(1);
            }
            //checks to see if endtime does not go into next day
            else {
                nextdecrementdate = LocalDate.now();
            }
        }
        auction.setNextdecrementtime(nextdecrementtime);
        auction.setNextdecrementdate(nextdecrementdate);
        auction.setDecrementinterval(decrementinterval);
        auction.setDecrementvalue(decrementvalue);
        auction.setDutchfinalprice(finalprice);
    }

    //helper to helper decrementandfinal method.
    private double getfinal(double startprice, int numberofdecrements, double decrementvalue) {
        double finalvalue = startprice;
        for(int i=0; i<numberofdecrements; i++){
            finalvalue = finalvalue - decrementvalue;
        }
        return finalvalue;
    }

    //creates successful bid for auction.
    private void createAndStoreBid(double bidprice, Auction auction, int sellerid) {
        Bid bid = new Bid();
        //saving the bidder.
        bid.setBidderid(auction.getHighestbidderid());
        //saving the bid price.
        bid.setBidprice(bidprice);
        //saving the seller of the auction.
        bid.setSellerid(sellerid);
        //saving auction id for reference.
        bid.setAuctionid(auction.getAuctionid());
        //storing bid
        bidRepository.save(bid);
    }

    //this will return all the bids.
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    //this will return all the bids for a specific auction.
    public List<Bid> getAllBidsById(int auctionid) {
        //local fields.
        List<Bid> allbids = bidRepository.findAll();;
        List<Bid> resultBids = new ArrayList<>();

        //checking to see if the bid corresponds to auction with auctionid.
        for(Bid b: allbids){
            if(b!=null && b.getAuctionid()==auctionid){
                resultBids.add(b);
            }
        }
        //no bids in bid table made for specific auction
        if(allbids.isEmpty()){
            return null;
        }
        else {
            return resultBids;
        }
    }

    public Auction getAuctionFromId(int id) {
        if(auctionRepository.findById(id).isEmpty()){
            return null;
        }
        else{
            if(auctionRepository.findById(id).get().isExpired()==false){
                return auctionRepository.findById(id).get();
            }
            else{
                return null;
            }
        }
    }

    public Auction getAuctionFromCatId(int auctioneditemid) {
        Auction result = null;
        List<Auction> auctionList = auctionRepository.findAll();
        for(int i=0; i<auctionList.size(); i++){
            if(auctionList.get(i)!=null && auctionList.get(i).isExpired()==false){
                if(auctionList.get(i).getAuctioneditemid()==auctioneditemid){
                    result = auctionList.get(i);
                }
            }
        }
        return result;
    }

    public String isValidExpired(int id) {
        Optional<Auction> opAuction = auctionRepository.findById(id);
        Auction auction = null;
        String result = "";
        if(opAuction.isEmpty()){
            result = "Non-existent";
        }
        else{
            auction = opAuction.get();
            //bid and expired - serve new page
            if(auction.getHighestbidderid()!=0 && auction.isExpired()==true){
                result = "Serve New Page";
            }
            //no bid and expired - invalid - dont serve new page at all - go back.
            else if(auction.getHighestbidderid()==0 && auction.isExpired()==true){
                result = "Go Back";
            }
            //bid and not yet expired - dont serve page yet but will.
            else if(auction.getHighestbidderid()!=0 && auction.isExpired()==false){
                result = "Don't Serve Yet";
            }
            //no bid and not yet expired - dont serve page yet - may or may not.
            else {
                result = "May or May not Serve ";
            }
        }
        return result;
    }

    public Auction getAuctionFromIdExpired(int id) {
        if (auctionRepository.findById(id).isPresent()){
            return auctionRepository.findById(id).get();
        }
        else{
            return null;
        }
    }

    //this will delete an auction.
    public void deleteAuctionAndCat(int auctionid) {
        //local fields.
        Optional<Auction> opAuction = auctionRepository.findById(auctionid);
        Auction auction;
        //auction does not exist.
        if(opAuction.isEmpty()){
            return;
        }
        //auction does exist; can remove it; if auction exists then so does the catalog
        else{
            auction = opAuction.get();
            catalogclient.removeFromCatalogById(auction.getAuctioneditemid());
            auctionRepository.delete(auction);
            //remove from catalog.
        }
    }

    //performs scheduling to indicate auction expired.
    @Scheduled(fixedRate = 1000)
    private void auctionExpired(){
        //local field.
        List<Auction> auctionlist;
        Catalog catitem;
        CatalogAndAuctionRequestBody catauction;

        //initialize list
        auctionlist = auctionRepository.findAll();

        //going through the entire auction repository.
        for(Auction auction: auctionlist){
            //need to first get the catalog item corresponding to the auction.
            catitem = catalogclient.searchCatalogById(auction.getAuctioneditemid());
            //finding auctions which have expired - add and remove appropriate information.
            if (auction.getEnddate().isBefore(LocalDate.now()) ||
               (auction.getEnddate().isEqual(LocalDate.now()) &&
                auction.getEndtime().equals(LocalTime.now().truncatedTo(ChronoUnit.SECONDS))) ||
               (auction.getEnddate().isEqual(LocalDate.now()) &&
                auction.getEndtime().isBefore(LocalTime.now().truncatedTo(ChronoUnit.SECONDS)))) {

                //load receipt information in the case that the expired auction has bids.
                //when highest bidderid is 0, no one has put in a bid.
                //do not need to load receipt info - can just delete auction straight away.
                if(auction.getHighestbidderid()==0 && auction.isExpired()==false){
                    auction.setExpired(true);
                    catalogclient.setCatalogAsExpired(auction.getAuctioneditemid());
                    deleteAuctionAndCat(auction.getAuctionid());
                }
                //load receipt information for expired auction that has been bid on and won.
                //make sure to save auction into auction table, where these won auctions
                //are only removed from auction if payment has been made.
                else{
                    //save updated auction into table - only in the first instance
                    //when the auction's expired flag has been set to be removed.
                    if(auction.isExpired()==false){
                        catauction = new CatalogAndAuctionRequestBody(catitem, auction);
                        paymentclient.loadPayInfoFromAuctionEndReciept(catauction);
                        auction.setExpired(true);
                        catalogclient.setCatalogAsExpired(auction.getAuctioneditemid());
                        auctionRepository.save(auction);
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    private void dutchAuctionDecremented(){
        List<Auction> auctionlist;
        double startprice;
        double decvalue;
        LocalTime decinterval;
        LocalTime nextdectime;
        LocalDate nextdecdate;

        //intialize list
        auctionlist = auctionRepository.findAll();

        //going through the entire auction list.
        for(Auction auction: auctionlist){
            //checks only dutch auctions that are live.
            if(auction.getAuctiontype().equals("Dutch") && auction.isExpired()==false){

                //checking to see if the current time is when the next
                //decrement should occur.
                if(LocalDate.now().isAfter(auction.getNextdecrementdate()) ||
                  (LocalDate.now().isEqual(auction.getNextdecrementdate()) &&
                   LocalTime.now().truncatedTo(ChronoUnit.SECONDS).isAfter(auction.getNextdecrementtime())) ||
                  (LocalDate.now().isEqual(auction.getNextdecrementdate()) &&
                   LocalTime.now().truncatedTo(ChronoUnit.SECONDS).equals(auction.getNextdecrementtime()))){

                    //the current price is not the final price, so can continue with more decrements.
                    if(auction.getStartprice()>auction.getDutchfinalprice()){
                        startprice = auction.getStartprice();
                        decvalue = auction.getDecrementvalue();
                        startprice = Math.round((startprice-decvalue)*100.0)/100.0;
                        nextdectime = auction.getNextdecrementtime();
                        nextdecdate = auction.getNextdecrementdate();
                        decinterval = auction.getDecrementinterval();
                        nextdectime = nextdectime.plusHours(decinterval.getHour())
                                .plusMinutes(decinterval.getMinute())
                                .plusSeconds(decinterval.getSecond()).truncatedTo(ChronoUnit.SECONDS);
                        if (nextdectime.isBefore(LocalTime.now())) {
                            // Time has rolled over to the next day
                            nextdecdate = nextdecdate.plusDays(1);
                        }
                        //checks to see if endtime does not go into next day
                        else {
                            nextdecdate = LocalDate.now();
                        }
                        auction.setStartprice(startprice);
                        auction.setNextdecrementtime(nextdectime);
                        auction.setNextdecrementdate(nextdecdate);
                        auctionRepository.save(auction);
                    }
                }
            }
        }
    }
}