package com.ecommerce.payment_service;

import com.ecommerce.payment_service.UIClasses.Winner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(path = "ecommerce/payment/ui")
public class ViewController {


    private final PaymentService paymentService;
    @Autowired
    public ViewController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/paymentpage/{itemid}")
    public String getpaymentpage(@PathVariable int itemid, Model model) {
        return paymentService.getpaymentpage(itemid, model);
    }
}

