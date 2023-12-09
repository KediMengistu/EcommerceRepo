package com.ecommerce.payment_service;

import com.ecommerce.payment_service.Client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "ecommerce/payment/paymentUI")
public class PaymentControllerUI {

    private final UserClient userClient;

    @Autowired
    public PaymentControllerUI(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/paymentSubmissionPage")
    public String paymentSubmissionPage(@RequestParam int auctionid, @RequestParam int itemid, @RequestParam int userid,
                                        @RequestParam int receiptid, @RequestParam String paytype,
                                        @CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "paymentSubmissionPage";
        }
        else{
            return "paymentSignOutPage";
        }
    }

    @GetMapping("/paymentFinalizedPage")
    public String paymentFinalizednPage(@RequestParam int auctionid, @RequestParam int itemid, @RequestParam int userid,
                                        @RequestParam int receiptid,
                                        @CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "paymentFinalizedPage";
        }
        else{
            return "paymentSignOutPage";
        }
    }

    @GetMapping("/paymentSignOutPage")
    public String paymentSignOutPage(@CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if (isValidSession) {
            return "paymentSubmissionPage";
        } else {
            return "paymentSignOutPage";
        }
    }
}
