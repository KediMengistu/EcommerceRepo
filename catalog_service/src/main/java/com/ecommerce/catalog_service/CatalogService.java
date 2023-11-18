package com.ecommerce.catalog_service;

import com.ecommerce.catalog_service.Client.AuctionClient;
import com.ecommerce.catalog_service.Client.UserClient;
import com.ecommerce.catalog_service.OtherServiceObjects.User;
import com.ecommerce.catalog_service.OutgoingRequestObjectBodies.CatalogAndTimeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CatalogService {

    private final UserClient userclient;
    private final AuctionClient auctionclient;
    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogService(CatalogRepository catalogRepository, UserClient userclient, AuctionClient auctionclient) {
        this.userclient = userclient;
        this.auctionclient = auctionclient;
        this.catalogRepository = catalogRepository;
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
                //first must store seller id
                catalog.setSellerid(seller.getUserid());

                //randomly assign shippingprice, and expeditedcost values.
                //assigned to be 0.0 to 999.99
                randomShipping = random.nextDouble() * 1000;
                randomExpedited = random.nextDouble() * 1000;
                catalog.setShippingprice(randomShipping);
                catalog.setExpeditedcost(randomExpedited);

                //obtain the endtime and enddate.
                timenow = LocalTime.now();
                datenow = LocalDate.now();
                catalogduration = catalog.getDuration();

                //calculates the endtime.
                endTime = timenow.plusHours(catalogduration.getHour())
                                 .plusMinutes(catalogduration.getMinute())
                                 .plusSeconds(catalogduration.getSecond());

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

                //creating request body to be supplied to auctionservice to create
                //auction for catalog item.

                catandtime = new CatalogAndTimeRequestBody(catalog, datenow, timenow, endTime);

                //saving item to catalog
                catalogRepository.save(catalog);

                //now need to put onto auction - need to
                //supply catalog request body parameter
                //considering this point is reached that means that the auction should be created
                //as seller and item have been fully validated.

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

    //searches and returns the catalog item corresponding to the id
    public Catalog searchCatalogById(int id) {
        if(catalogRepository.findById(id).isEmpty()){
            return null;
        }
        else{
            return catalogRepository.findById(id).get();
        }
    }
    //returns the entire catalog
    public List<Catalog> entireCatalog() {
        return catalogRepository.findAll();
    }

    //searches and returns the list of catalog items with the specific name.
    public List<Catalog> searchCatalog(String itemname) {
        //local fields.
        List<Catalog> catItems = catalogRepository.findAll();
        List<Catalog> resultItems = new ArrayList<>();

        //checking to see if the catalog items contain the keyword
        for(Catalog c: catItems){
            //true means, the current catalog item has the keyword - added to return list.
            if(c!= null && c.getItemname()!= null && !c.getItemname().isEmpty() &&
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

    //peforms checks to validate catalog item to be potentially sold.
    private boolean peformCatalogItemChecks(Catalog catalog) {
        //catalog name check
        String catalogname = catalog.getItemname();
        if(catalogname==null || catalogname.isEmpty()){
            return false;
        }
        //catalog description check
        String catalogdescription = catalog.getItemdescription();
        if(catalogname==null || catalogname.isEmpty()){
            return false;
        }
        //catalog auction check
        String catalogauctiontype = catalog.getAuctiontype();
        if(catalogname==null ||
           catalogname.isEmpty() ||
           (!catalogauctiontype.equals("Forward") &&
           !catalogauctiontype.equals("Dutch"))){
            return false;
        }
        //catalog startprice check
        double catalogstartprice = catalog.getStartprice();
        if(catalogstartprice < 0){
            return false;
        }
        //catalog duration check
        LocalTime catalogduration = catalog.getDuration();
        if(catalogduration==null ||
           catalogduration.getHour()<0 ||
           catalogduration.getMinute()<0 ||
           catalogduration.getSecond()<0){
            return false;
        }
        return true;
    }
}
