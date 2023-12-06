package com.ecommerce.payment_service;

import com.ecommerce.payment_service.UIClasses.Winner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(path = "ecommerce/payment/ui")
public class ViewController {

    @Autowired
    private static PaymentService paymentService;
    @GetMapping("/paymentpage")
    public String getpaymentpage(Model model) {
        Winner winner = new Winner();

        model.addAttribute("winner", winner);

        return "payment";
    }
}

