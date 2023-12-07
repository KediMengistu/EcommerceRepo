package com.ecommerce.payment_service;

import com.ecommerce.payment_service.IncomingRequestObjectBodies.CatalogAndAuctionRequestBody;
import com.ecommerce.payment_service.IncomingRequestObjectBodies.PaymentInfo;
import com.ecommerce.payment_service.Receipt.Receipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "ecommerce/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //creates the receipt for the ended auction.
    @PostMapping("/load")
    void loadPayInfoFromAuctionEndReciept(@RequestBody CatalogAndAuctionRequestBody catauction){
         paymentService.load(catauction);
    }

    //retuns list of receipt info for auctions that have been noted as expired.
    @GetMapping("/allauctionreceipt")
    List<Receipt> getAllReceipts(){
        return paymentService.getAllReceipts();
    }

    //creates payment info by initially verifying validity with receipts.


    //returns list of payment for all auctions that have been paid for.
    @GetMapping("/allpayment")
    List<Payment> getAllPayment(){
        return paymentService.getAllPaymentInfo();
    }

}