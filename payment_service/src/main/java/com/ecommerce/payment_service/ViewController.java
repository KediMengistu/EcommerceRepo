package com.ecommerce.payment_service;

import com.ecommerce.payment_service.IncomingRequestObjectBodies.PaymentInfo;
import com.ecommerce.payment_service.UIClasses.Winner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "ecommerce/payment/ui")
public class ViewController {


    private final PaymentService paymentService;
    @Autowired
    public ViewController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/paymentpage/{itemid}")
    public String getpaymentpage(@PathVariable int itemid, @RequestParam int userid, @RequestParam boolean expedited, Model model) {
        return paymentService.getpaymentpage(itemid, userid, expedited, model);
    }
    @PostMapping("/insertpaymentinfo")
    String payForAuctionedOffItem(@RequestBody PaymentInfo paymentInfo, Model model){
        return paymentService.payForItem(paymentInfo, model);
    }

    @GetMapping("/reciept/{paymentid}")
    public String getreciept(@PathVariable int paymentid, @RequestParam int itemid, @RequestParam int userid, Model model) {
        return paymentService.getreciept(paymentid, itemid, userid, model);
    }
}

