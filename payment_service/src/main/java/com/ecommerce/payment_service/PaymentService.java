package com.ecommerce.payment_service;

import com.ecommerce.payment_service.Client.AuctionClient;
import com.ecommerce.payment_service.Client.UserClient;
import com.ecommerce.payment_service.IncomingRequestObjectBodies.CatalogAndAuctionRequestBody;
import com.ecommerce.payment_service.IncomingRequestObjectBodies.PaymentInfo;
import com.ecommerce.payment_service.OtherServiceObjects.Auction;
import com.ecommerce.payment_service.OtherServiceObjects.Catalog;
import com.ecommerce.payment_service.OtherServiceObjects.User;
import com.ecommerce.payment_service.Receipt.Receipt;
import com.ecommerce.payment_service.Receipt.ReceiptRepository;
import com.ecommerce.payment_service.UIClasses.Reciept;
import com.ecommerce.payment_service.UIClasses.Winner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final UserClient userclient;
    private final AuctionClient auctionclient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ReceiptRepository receiptRepository, UserClient userclient, AuctionClient auctionclient) {
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
        this.userclient = userclient;
        this.auctionclient = auctionclient;
    }

    //creates receipt info.
    public void load(CatalogAndAuctionRequestBody catauction) {
        //local fields.
        Catalog cat;
        Auction auction;
        Receipt receipt;
        double totalcost;

        //extract and save catalog and auction information for item being paid for.
        cat = catauction.getCatalog();
        auction = catauction.getAuction();

        //need to first check if the auction has had any bids; if not that means the highestbidderid = 0
        //that means no user has made a bid for the auctioned item and so no receipt is generated
        //for that auction - essentially we are filtering for and storing only auctions that have bids.
        if(auction.getHighestbidderid()==0){
            return;
        }
        //there is a bidder for the ended auction and so payment can be generated for that auction.
        else{
            //need to check if there arent any existing receipt entries for the same auction.
            //if there is then receipt will not be created.
            if(!receiptRepository.findByauctionid(auction.getAuctionid()).isEmpty()){
                return;
            }
            else{
                //create and set parameters for receipt obj to be stored.
                receipt = new Receipt();
                //id for paid auction derived from auction table.
                receipt.setAuctionid(auction.getAuctionid());
                //id for paid item derived from auction table.
                receipt.setItemid(auction.getAuctioneditemid());
                //name for paid item derived from catalog table.
                receipt.setItemname(cat.getItemname());
                //description for paid item derived from catalog table.
                receipt.setItemdescription(cat.getItemdescription());
                //seller for paid item derived from catalog table.
                receipt.setSellerid(cat.getSellerid());
                //id for auction winner derived from auction table.
                receipt.setPayerid(auction.getHighestbidderid());
                //style of pay for paid item is derived from auction table.
                receipt.setAuctionstyle(auction.getAuctiontype());
                //submitted winning bid derived from auction table.
                receipt.setSubmittedbid(auction.getHighestbid());
                //shipping price for paid item derived from catalog table.
                receipt.setShippingprice(cat.getShippingprice());
                //expedited cost for paid item derived from catalog table.
                receipt.setExpeditedcost(cat.getExpeditedcost());

                //obtaining default total.
                //default includes no expedited shipping
                totalcost = cat.getShippingprice() + auction.getHighestbid();
                totalcost = Math.round(totalcost * 100.0)/100.0;
                receipt.setDefaulttotal(totalcost);

                //save payment in payment table.
                receiptRepository.save(receipt);
            }
        }
    }

    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    //used to pay for item that was won.
    public String payForItem(PaymentInfo paymentInfo,Model model) {
        //local fields.
        User user;
        Optional<Receipt> opReceipt;
        Receipt receipt;
        Payment pay;
        boolean payInfoIsValid = false;

        //try to extract the user from the user table with the specific unique username.
        user = userclient.findPayerFromUsername(paymentInfo.getUsername());

        //user does not exist, so cannot pay for item.
        if(user==null){
            return "false";
        }
        //user does exist, so can potentially pay for item.
        else{
            //extract receipt entry from unique auctionid.
            opReceipt = receiptRepository.findByauctionid(paymentInfo.getPaidauctionid());

            //there does not exist a receipt entry corresponding to the auction with auctionid.
            if(opReceipt.isEmpty()){
                return "false";
            }
            //receipt entry exists with the unique auctionid.
            else{
                //save receipt in placeholder.
                receipt = opReceipt.get();

                //checking to see if there doesnt already exist a payment corresponding to the receipt.
                if(!paymentRepository.findByreceiptid(receipt.getReceiptid()).isEmpty()) {
                    return "paidfor";
                }
                else{
                    //check to see if the user corresponds to the receipt with auctionid.
                    //the user will be confirmed as the registered payer/bid winner.
                    if(user.getUserid()==receipt.getPayerid()){
                        //create payment information if all is valid.
                        payInfoIsValid = checkPayInfo(paymentInfo.getCardnum(), paymentInfo.getCardfname(), paymentInfo.getCardlname(), paymentInfo.getExpdate(), paymentInfo.getSecuritycode());
                        if(payInfoIsValid) {
                            pay = new Payment();
                            pay.setUsercardnumber(paymentInfo.getCardnum());
                            pay.setUsercardfname(paymentInfo.getCardfname());
                            pay.setUsercardlname(paymentInfo.getCardlname());
                            pay.setUsercardexpdate(paymentInfo.getExpdate());
                            pay.setUsercardsecuritycode(paymentInfo.getSecuritycode());
                            pay.setReceiptid(receipt.getReceiptid());
                            pay.setTotalpaid(paymentInfo.getTotalpaid());
                            paymentRepository.save(pay);

                            //once payment for auction is confirmed, auction can finally be removed.
                            auctionclient.deleteAuction(receipt.getAuctionid());
                            return getreciept(pay.getPaymentid(), receipt.getAuctionid(), user.getUserid(), model);
                        }
                    }
                    return "notwinner";
                }

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

    //provides a list of all the payments.
    List<Payment> getAllPaymentInfo(){
        return paymentRepository.findAll();
    }

    public String getpaymentpage(int itemid, int userid, boolean expidited, Model model) {
         //check for sessionid/check user
        Receipt receipt = receiptRepository.findByauctionid(itemid).get();

        if(userid != receipt.getPayerid())
            return "notwinner";
        Double totalshipping;
        if(expidited){
        totalshipping = receipt.getShippingprice() + receipt.getExpeditedcost();}
        else  totalshipping = receipt.getShippingprice() + receipt.getExpeditedcost();

        User user = userclient.findPayerFromId(userid);
        Winner winner = new Winner(user.getFirstname(), user.getLastname(),
                user.getStreetname(), user.getStreetnumber(), user.getCity(),
                user.getCountry(), user.getPostalcode(), receipt.getItemname(),
                itemid, receipt.getPayerid(), totalshipping, receipt.getDefaulttotal() + totalshipping );

        model.addAttribute("winner", winner);

        return "payment";
    }

    public String getreciept(int paymentid, int itemid, int userid, Model model) {
        Receipt receipt = receiptRepository.findByauctionid(itemid).get();
        Optional<Payment> payment = paymentRepository.findById(paymentid);

        if(userid != receipt.getPayerid() || payment.get().getReceiptid() != receipt.getReceiptid())
            return "false";

        if(payment.isEmpty())
            return "notpaid";

        User user = userclient.findPayerFromId(userid);
         Reciept receiptobject  = new Reciept(user.getFirstname(), user.getLastname(),
                user.getStreetname(), user.getStreetnumber(), user.getCity(),
                user.getCountry(), user.getPostalcode(), receipt.getItemname(),
                itemid, payment.get().getTotalpaid(), 10);

        model.addAttribute("winner", receiptobject);

        return "reciept";
    }
}