package com.ecommerce.payment_service;

import com.ecommerce.payment_service.IncomingRequestObjectBodies.CatalogAndAuctionRequestBody;
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

    @PostMapping("/load")
    void loadPayInfoFromAuctionEnd(@RequestBody CatalogAndAuctionRequestBody catauction){
         paymentService.load(catauction);
    }

    @GetMapping("/allpayment")
    List<Payment> getAllPayment(){
        return paymentService.getAllPaymentInfo();
    }

    //updates currently existing payment info with input parameters.
    @PutMapping("/insertpaymentinfo")
    boolean payForAuctionedOffItem(@RequestParam String username,
                                   @RequestParam int paidauctionid,
                                   @RequestParam int cardnum,
                                   @RequestParam String cardfname,
                                   @RequestParam String cardlname,
                                   @RequestParam LocalDate expdate,
                                   @RequestParam int securitycode){
        return paymentService.payForItem(username, paidauctionid, cardnum, cardfname, cardlname, expdate, securitycode);
    }


}