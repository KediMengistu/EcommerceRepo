package com.ecommerce.payment_service;

import com.ecommerce.payment_service.Client.UserClient;
import com.ecommerce.payment_service.IncomingRequestObjectBodies.CatalogAndAuctionRequestBody;
import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import com.ecommerce.payment_service.OtherServiceObjects.Catalog;
import com.ecommerce.payment_service.OtherServiceObjects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserClient userclient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserClient userclient) {
        this.paymentRepository = paymentRepository;
        this.userclient = userclient;
    }

    //creates payment info
    public void load(CatalogAndAuctionRequestBody catauction) {
        //local fields.
        Catalog cat;
        Auction auction;
        Payment pay;
        double totalcost;

        //extract and save catalog and auction information for item being paid for.
        cat = catauction.getCatalog();
        auction = catauction.getAuction();

        //need to first check if the auction has had any bids; if not that means the highestbidderid = 0
        //that means no user has made a bid for the auctioned item and so no payment info is generated
        //for that auction - essentially we are filtering for and storing only payments for auctions that have bids.
        if(auction.getHighestbidderid()==0){
            return;
        }
        //there is a bidder for the ended auction and so payment can be generated for that auction.
        else{
            //create and set parameters for payment obj to be stored.
            pay = new Payment();
            //id for paid auction derived from auction table.
            pay.setPaidauctionid(auction.getAuctionid());
            //id for paid item derived from auction table.
            pay.setPaiditemid(auction.getAuctioneditemid());
            //name for paid item derived from catalog table.
            pay.setPaiditemname(cat.getItemname());
            //description for paid item derived from catalog table.
            pay.setPaiditemdescription(cat.getItemdescription());
            //seller for paid item derived from catalog table.
            pay.setSellerid(cat.getSellerid());
            //id for auction winner derived from auction table.
            pay.setPayerid(auction.getHighestbidderid());
            //style of pay for paid item is derived from auction table.
            pay.setPaymentstyle(auction.getAuctiontype());
            //submitted winning bid derived from auction table.
            pay.setSubmittedbid(auction.getHighestbid());
            //shipping price for paid item derived from catalog table.
            pay.setShippingprice(cat.getShippingprice());
            //expedited cost for paid item derived from catalog table.
            pay.setExpeditedcost(cat.getExpeditedcost());

            //obtaining default total.
            //default includes no expedited shipping
            totalcost = cat.getShippingprice() + auction.getHighestbid();
            totalcost = Math.round(totalcost * 100.0)/100.0;
            pay.setDefaulttotal(totalcost);

            //save payment in payment table.
            paymentRepository.save(pay);
        }
    }

    //provides a list of all the payments.
    List<Payment> getAllPaymentInfo(){
        return paymentRepository.findAll();
    }

    //used to pay for item that was won.
    public boolean payForItem(String username, int paidauctionid, int cardnum, String cardfname, String cardlname, LocalDate expdate, int securitycode) {
        //local fields.
        User user;
        Optional<Payment> opPay;
        Payment pay;
        boolean payInfoIsValid = false;

        //try to extract the user from the user table with the specific unique username.
        user = userclient.findPayerFromUsername(username);

        //user does not exist, so cannot pay for item.
        if(user==null){
            return false;
        }
        //user does exist, so can potentially pay for item.
        else{
            //extract payment entry from unique paidauctionid.
            opPay = paymentRepository.findBypaidauctionid(paidauctionid);

            //there does not exist a payment entry corresponding to the user with input username.
            if(opPay.isEmpty()){
                return false;
            }
            //payment entry exists with the unique paidauctionid.
            else{
                //save payment in placeholder.
                pay = opPay.get();

                //check to see if the user corresponds to the payment with paidauctionid.
                //the user will be confirmed as the registered payer/bid winner.
                if(user.getUserid()==pay.getPayerid()){
                    //update payment information if all is valid.
                    payInfoIsValid = checkPayInfo(cardnum, cardfname, cardlname, expdate, securitycode);
                    if(payInfoIsValid) {
                        pay.setUsercardnumber(cardnum);
                        pay.setUsercardfname(cardfname);
                        pay.setUsercardlname(cardlname);
                        pay.setUsercardexpdate(expdate);
                        pay.setUsercardsecuritycode(securitycode);
                        paymentRepository.save(pay);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }
    }

    private boolean checkPayInfo(int cardnum, String cardfname, String cardlname, LocalDate expdate, int securitycode) {
        if(String.valueOf(cardnum).length() != 9){
            return false;
        }
        if(cardfname==null || cardfname.isEmpty()){
            return false;
        }
        if(cardlname==null || cardlname.isEmpty()){
            return false;
        }
        if(expdate.isBefore(LocalDate.now())){
            return false;
        }
        if(String.valueOf(securitycode).length() != 3){
            return false;
        }
        return true;
    }
}