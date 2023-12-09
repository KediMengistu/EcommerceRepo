package com.ecommerce.catalog_service;

import com.ecommerce.catalog_service.Client.AuctionClient;
import com.ecommerce.catalog_service.Client.UserClient;
import com.ecommerce.catalog_service.OtherServiceObjects.User;
import com.ecommerce.catalog_service.OutgoingRequestObjectBodies.CatalogAndTimeRequestBody;
import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;
    private final UserClient userclient;
    private final AuctionClient auctionclient;

    @Autowired
    public CatalogService(CatalogRepository catalogRepository, UserClient userclient, AuctionClient auctionclient) {
        this.catalogRepository = catalogRepository;
        this.userclient = userclient;
        this.auctionclient = auctionclient;
    }

    public boolean sellOnCatalog(Catalog catalog, String sellerusername) {
        //local fields.
        boolean itemIsValid;
        Random random = new Random();
        double randomShipping;
        double randomExpedited;
        LocalTime timenow;
        LocalDate datenow;
        LocalTime catalogduration;
        LocalTime endTime;
        LocalDate endDate;
        CatalogAndTimeRequestBody catandtime;

        //Check to see if seller of item is a user in system.
        User seller = userclient.findSellerFromUsername(sellerusername);

        //the username provided is accurate as it corresponds to a unique user - validate
        //catalog information before placing onto catalog.
        if(seller!=null){
            //checks if item is valid and can be uploaded.
            itemIsValid = peformCatalogItemChecks(catalog);

            //item is valid - can be uploaded to catalog.
            if(itemIsValid==true){
                //first must store seller id.
                catalog.setSellerid(seller.getUserid());

                //do rounding of price to make sure 2 decimal is mainted.
                catalog.setStartprice(Math.round(catalog.getStartprice()*100.0)/100.0);

                //randomly assign shippingprice, and expeditedcost values.
                //assigned to be 0.0 to 999.99.
                randomShipping = random.nextDouble() * 1000;
                randomExpedited = random.nextDouble() * 1000;
                catalog.setShippingprice(Math.round(randomShipping*100.0)/100.0);
                catalog.setExpeditedcost(Math.round(randomExpedited*100.0)/100.0);

                //obtain the endtime and enddate.
                timenow = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                datenow = LocalDate.now();
                catalogduration = catalog.getDuration().truncatedTo(ChronoUnit.SECONDS);

                //calculates the endtime.
                endTime = timenow.plusHours(catalogduration.getHour())
                                 .plusMinutes(catalogduration.getMinute())
                                 .plusSeconds(catalogduration.getSecond()).truncatedTo(ChronoUnit.SECONDS);

                //checks to see if endtime goes into the next day.
                if (endTime.isBefore(timenow)) {
                    // Time has rolled over to the next day
                    endDate = datenow.plusDays(1);
                }
                //checks to see if endtime does not go into next day
                else {
                    endDate = datenow;
                }

                //setting end date for catalog item.
                catalog.setEnddate(endDate);

                //set catalog item to not expired
                catalog.setExpired(false);

                //creating request body to be supplied to auctionservice to create
                //auction for catalog item - JSON contains catalog and auction time info
                //facade pattern is technically used here because user has no control or
                //view of auction being created

                catandtime = new CatalogAndTimeRequestBody(catalog, datenow, timenow, endTime);

                //saving item to catalog
                catalogRepository.save(catalog);

                //now need to put onto auction - need to
                //supply catalog request body parameter.
                //considering this point is reached that means that the auction should be created
                //as seller and item have been fully validated; true will be returned to indicate this.

                return auctionclient.createAuctionFromCatTimeItem(catandtime);
            }
            //item is invalid - cannot be uploaded to catalog.
            else{
                return false;
            }
        }
        //the username provided is inaccurate as it does not correspond to a unique user.
        else{
            return false;
        }
    }

    //searches and returns the catalog item corresponding to the id.
    //added filter to only be searching for non-expired catalog items.
    public Catalog searchCatalogById(int id) {
        if(catalogRepository.findById(id).isEmpty()){
            return null;
        }
        else{
            if(catalogRepository.findById(id).get().getExpired()==false){
                return catalogRepository.findById(id).get();
            }
            else{
                return null;
            }
        }
    }

    //returns the entire catalog.
    //added filter to only return non-expired catalog items.
    public List<Catalog> entireCatalog() {
        List<Catalog> repo = catalogRepository.findAll();
        List<Catalog> result = new ArrayList<>();
        //filtering repo arraylist of catalog items which are non-expired.
        for(int i=0; i<repo.size(); i++){
            if(repo.get(i)!=null && repo.get(i).getExpired()==false){
                result.add(repo.get(i));
            }
        }
        return result;
    }

    //searches and returns the list of catalog items with the specific name.
    //added filter to only return non-expired catalog items.
    public List<Catalog> searchCatalog(String itemname) {
        //local fields.
        List<Catalog> catItems = catalogRepository.findAll();
        List<Catalog> resultItems = new ArrayList<>();

        //checking to see if the catalog items contain the keyword
        for(Catalog c: catItems){
            //true means, the current catalog item has the keyword - added to return list.
            if(c!= null && c.getExpired()==false && c.getItemname()!= null && !c.getItemname().isEmpty() &&
                c.getItemname().contains(itemname)){
                resultItems.add(c);
            }
        }
        //no items in catalog contain the keyword - empty list.
        if(resultItems.isEmpty()){
            return null;
        }
        //items exist in catalog which contain the keyword - non-empty list.
        else{
            return resultItems;
        }
    }

    //sets the catalog item to be expired.
    public void setItemAsExpired(int id) {
        if(catalogRepository.findById(id).isPresent()){
            Catalog cat = catalogRepository.findById(id).get();
            cat.setExpired(true);
            catalogRepository.save(cat);
        }
    }

    //removes the catalog item by its id.
    public void removeItem(int id) {
        if(catalogRepository.findById(id).isPresent()){
            Catalog cat = catalogRepository.findById(id).get();
            if(cat.getExpired()==true){
                catalogRepository.deleteById(id);
            }
        }
    }

    public Auction getAuctionFromCatId(int auctioneditemid) {
        return auctionclient.getAuctionFromCatId(auctioneditemid);
    }

    //peforms checks to validate catalog item to be potentially sold.
    private boolean peformCatalogItemChecks(Catalog catalog) {
        //catalog name check
        String catalogname = catalog.getItemname();
        if(catalogname==null || catalogname.isEmpty()){
            return false;
        }
        //catalog description check
        String catalogdescription = catalog.getItemdescription();
        if(catalogdescription==null || catalogdescription.isEmpty()){
            return false;
        }
        //catalog auction check
        String catalogauctiontype = catalog.getAuctiontype();
        if(catalogauctiontype==null ||
           catalogauctiontype.isEmpty() ||
           (!catalogauctiontype.equals("Forward") &&
           !catalogauctiontype.equals("Dutch"))){
            return false;
        }
        //catalog startprice check
        //must be positve and at most 2 decimal places long
        double catalogstartprice = catalog.getStartprice();
        String priceStr = String.format("%.2f", catalogstartprice);
        String regex = "\\d+\\.\\d{2}";
        if(catalogstartprice <= 0 || !priceStr.matches(regex)){
            return false;
        }
        //catalog duration check
        //c1 = checks if duration is null (it is not specified).
        //c2 = checks if duration is 0hours 0min 0seconds - we cannot have a duration of 0 time.
        //c3 = checks if any parts of the duration are negative - cannot have negative hours, min, or seconds.
        //c4 = checks if duration is greater than 23hrs, 59 min, and 59 seconds.
        //c5 = checks if total duration is less than 1 minute - we must have a catolog item be on auction for 60sec or 1 min.
        LocalTime catalogduration = catalog.getDuration();
        if (catalogduration == null ||
           (catalogduration.getHour() == 0 && catalogduration.getMinute() == 0 && catalogduration.getSecond() == 0) ||
            catalogduration.getHour() < 0 || catalogduration.getMinute() < 0 || catalogduration.getSecond() < 0 ||
            catalogduration.getHour() > 23 || catalogduration.getMinute() > 59 || catalogduration.getSecond() > 59 ||
           (catalogduration.getHour() * 3600 + catalogduration.getMinute() * 60 + catalogduration.getSecond() < 60))
        {
            return false;
        }
        return true;
    }

    public Catalog searchCatalogFromIdExpired(int id) {
        if (catalogRepository.findById(id).isPresent()){
            return catalogRepository.findById(id).get();
        }
        else{
            return null;
        }
    }
}