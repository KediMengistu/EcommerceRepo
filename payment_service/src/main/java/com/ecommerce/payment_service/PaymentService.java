package com.ecommerce.payment_service;

import com.ecommerce.payment_service.Client.UserClient;
import com.ecommerce.payment_service.IncomingRequestObjectBodies.CatalogAndAuctionRequestBody;
import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import com.ecommerce.payment_service.OtherServiceObjects.Catalog;
import com.ecommerce.payment_service.OtherServiceObjects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        //create and set parameters for payment obj to be stored.
        pay = new Payment();
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

    //provides a list of all the payments.
    List<Payment> getAllPaymentInfo(){
        return paymentRepository.findAll();
    }

    //used to pay for item that was won.
    public boolean payForItem(String username) {
        //local fields.
        User user;
        Optional<Payment> opPay;
        Payment pay;

        //try to extract the user from the user table with the specific unique username.
        user = userclient.findPayerFromUsername(username);

        //user does not exist, so cannot pay for item.
        if(user==null){
            return false;
        }
        //user does exist, so can potentially pay for item.
        else{
            //extract the payment info corresponding to the user whose username is the input
            //and the associated id.
            opPay = paymentRepository.findBypayerid(user.getUserid());

            //there does not exist a payment entry corresponding to the user with input username.
            if(opPay.isEmpty()){
                return false;
            }
            //payment entry exists for payer with username
            else{
                //save payment in placeholder
                pay = opPay.get();

                //can display the winner information, payment information and the transaction b/w payer and seller, etc.
                return true;
            }
        }
    }
}